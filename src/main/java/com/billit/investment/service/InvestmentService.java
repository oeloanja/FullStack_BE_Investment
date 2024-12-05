package com.billit.investment.service;

import com.billit.investment.connect.loan_group.client.LoanGroupServiceClient;
import com.billit.investment.connect.loan_group.dto.LoanGroupRequestDto;
import com.billit.investment.connect.user.client.UserServiceClient;
import com.billit.investment.connect.user.dto.UserServiceRequestDto;
import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestmentActualReturnRate;
import com.billit.investment.dto.*;
import com.billit.investment.repository.InvestStatusRepository;
import com.billit.investment.repository.InvestmentActualReturnRateRepository;
import com.billit.investment.repository.InvestmentRepository;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final InvestStatusRepository investStatusRepository;
    private final InvestmentActualReturnRateRepository investmentActualReturnRateRepository;
    private final InvestmentPortfolioService investmentPortfolioService;
    private final SettlementService settlementService;
    private final UserServiceClient userServiceClient;
    private final LoanGroupServiceClient loanGroupServiceClient;

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    private final BigDecimal BILLIT_CHARGE = new BigDecimal("0.5");

    @Transactional
    public List<Investment> createInvestments(List<InvestmentCreateRequest> requests) {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Investment request list is empty");
        }

        List<Investment> investments = requests.stream()
                .map(request -> {
                    try {
                        // 1. Investment 엔티티 생성 및 저장
                        Investment investment = new Investment();
                        investment.setGroupId(request.getGroupId());
                        investment.setUserInvestorId(request.getUserInvestorId());
                        investment.setAccountInvestorId(request.getAccountInvestorId());
                        investment.setInvestmentAmount(request.getInvestmentAmount());
                        investment.setExpectedReturnRate(request.getExpectedReturnRate());

                        Investment savedInvestment = investmentRepository.save(investment);
                        investmentRepository.flush(); // DB에 즉시 반영

                        // 2. Investment Status 생성
                        InvestStatus investStatus = new InvestStatus();
                        investStatus.setInvestmentId(savedInvestment.getInvestmentId());
                        investStatus.setInvestStatusType(InvestStatusType.WAITING);
                        investStatusRepository.save(investStatus);
                        investStatusRepository.flush(); // DB에 즉시 반영

                        // 3. Investment Portfolio 생성(없는 경우에만)
                        InvestmentPortfolioRequest investmentPortfolioRequest = new InvestmentPortfolioRequest();
                        investmentPortfolioRequest.setUserInvestorId(request.getUserInvestorId());
                        if(!investmentPortfolioService.isExistPortfolio(investmentPortfolioRequest)){
                            investmentPortfolioService.createInvestmentPortfolio(investmentPortfolioRequest);
                        }

                        // 4. 투자금 처리
                        processInvestmentTransaction(request);

                        return savedInvestment;
                    } catch (Exception e) {
                        log.error("Failed to process investment request: {}", request, e);
                        throw e; // 예외 발생시 트랜잭션 롤백
                    }
                })
                .collect(Collectors.toList());

        entityManager.clear();
        return investments;
    }

    private void processInvestmentTransaction(InvestmentCreateRequest request) {
        if (request.getGroupId() == null || request.getGroupId()<0) {
            throw new IllegalArgumentException("Invalid groupId: " + request.getGroupId());
        }
        if (request.getInvestmentAmount() == null || request.getInvestmentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid investmentAmount: " + request.getInvestmentAmount());
        }

        LoanGroupRequestDto loanRequest = new LoanGroupRequestDto(request.getGroupId(), request.getInvestmentAmount());
        UserServiceRequestDto userRequest = new UserServiceRequestDto(
                request.getAccountInvestorId(),
                request.getInvestmentAmount(),
                "투자금 출금");

        // 투자금 출금 요청
        log.debug("Requesting withdrawal for user: {}", request.getUserInvestorId());
        userServiceClient.withdrawInvest(Long.valueOf(request.getUserInvestorId()), userRequest);

        // 투자금 입금 요청
        log.debug("Requesting platform account update for group: {}", request.getGroupId());
        try {
            loanGroupServiceClient.updatePlatformAccountBalance(loanRequest);
        } catch (FeignException e) {
            log.error("Feign call failed: status={}, body={}", e.status(), e.contentUTF8(), e);
            throw new RuntimeException("투자금 입금 실패: " + e.contentUTF8(), e);
        }
    }

    public List<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse> getAllInvestmentWithInvestStatusWithInvestmentActualRate() {
        List<Object[]> results = investmentRepository.findInvestmentWithStatus();
        return results.stream()
                .map(result -> {
                    Investment investment = (Investment) result[0];
                    InvestStatus investStatus = (InvestStatus) result[1];
                    InvestmentActualReturnRate investmentActualReturnRate = getLatestInvestmentActualReturnRateByInvestmentId(investment.getInvestmentId());

                    return InvestmentWithInvestStatusWithInvestmentActualRateGetResponse.builder()
                            .investmentId(investment.getInvestmentId())
                            .groupId(investment.getGroupId())
                            .userInvestorId(investment.getUserInvestorId())
                            .accountInvestorId(investment.getAccountInvestorId())
                            .investmentAmount(investment.getInvestmentAmount())
                            .investmentDate(investment.getInvestmentDate())
                            .expectedReturnRate(investment.getExpectedReturnRate())
                            .createdAt(investment.getCreatedAt())
                            .investStatusType(investStatus.getInvestStatusType().getCode())
                            .settlementRatio(investment.getSettlementRatio())
                            .actualReturnRate(investmentActualReturnRate != null ? investmentActualReturnRate.getActualReturnRate() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse> getInvestmentWithInvestStatusWithInvestmentActualRateByInvestorId(Integer userInvestorId) {
        List<Object[]> results = investmentRepository.findInvestmentWithStatusByUserInvestorId(userInvestorId);
        return results.stream()
                .map(result -> {
                    Investment investment = (Investment) result[0];
                    InvestStatus investStatus = (InvestStatus) result[1];
                    InvestmentActualReturnRate investmentActualReturnRate = getLatestInvestmentActualReturnRateByInvestmentId(investment.getInvestmentId());

                    return InvestmentWithInvestStatusWithInvestmentActualRateGetResponse.builder()
                            .investmentId(investment.getInvestmentId())
                            .groupId(investment.getGroupId())
                            .userInvestorId(investment.getUserInvestorId())
                            .accountInvestorId(investment.getAccountInvestorId())
                            .investmentAmount(investment.getInvestmentAmount())
                            .investmentDate(investment.getInvestmentDate())
                            .expectedReturnRate(investment.getExpectedReturnRate())
                            .createdAt(investment.getCreatedAt())
                            .investStatusType(investStatus.getInvestStatusType().getCode())
                            .settlementRatio(investment.getSettlementRatio())
                            .actualReturnRate(investmentActualReturnRate != null ? investmentActualReturnRate.getActualReturnRate() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public InvestmentWithInvestStatusWithInvestmentActualRateGetResponse getInvestmentWithInvestStatusWithInvestmentActualRateByinvestmentId(Integer investmentId) {
        Optional<InvestmentWithStatusDTO> resultOptional = investmentRepository.findInvestmentWithStatusByInvestmentId(investmentId);

        InvestmentWithStatusDTO result = resultOptional.orElseThrow(() ->
                new IllegalArgumentException("Investment not found : " + investmentId + " is Wrong investment id"));

        Investment investment = result.getInvestment();
        InvestStatus investStatus = result.getInvestStatus();
        InvestmentActualReturnRate investmentActualReturnRate = getLatestInvestmentActualReturnRateByInvestmentId(investment.getInvestmentId());

        return InvestmentWithInvestStatusWithInvestmentActualRateGetResponse.builder()
                .investmentId(investment.getInvestmentId())
                .groupId(investment.getGroupId())
                .userInvestorId(investment.getUserInvestorId())
                .accountInvestorId(investment.getAccountInvestorId())
                .investmentAmount(investment.getInvestmentAmount())
                .investmentDate(investment.getInvestmentDate())
                .expectedReturnRate(investment.getExpectedReturnRate())
                .createdAt(investment.getCreatedAt())
                .investStatusType(investStatus.getInvestStatusType().getCode())
                .settlementRatio(investment.getSettlementRatio())
                .actualReturnRate(investmentActualReturnRate != null ? investmentActualReturnRate.getActualReturnRate() : null)
                .build();
    }

    // 현숙언니가 대출군에서 필요금액 모집 마감 확인하고 불러줘야 하는 api : 투자실행일시 업데이트
    @Transactional
    public void updateInvestmentDatesByGroupId(Integer groupId) {
        List<Investment> investments = investmentRepository.findByGroupId(groupId);
        if (investments.isEmpty()) {
            throw new IllegalArgumentException("No investments found for groupId: " + groupId);
        }

        try {
            investments.forEach(investment -> {
                investment.setInvestmentDate(LocalDateTime.now());
                // 상태 업데이트를 한 번의 트랜잭션으로 처리
                InvestStatus status = investStatusRepository.findById(investment.getInvestmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Investment status not found"));
                status.setInvestStatusType(InvestStatusType.valueOf("EXECUTING"));
                investStatusRepository.save(status);

                // 포트폴리오 상태 업데이트
                InvestmentPortfolioRequest investmentPortfolioRequest = new InvestmentPortfolioRequest();
                investmentPortfolioRequest.setUserInvestorId(investment.getUserInvestorId());
                investmentPortfolioService.updatePortfolioTotalInvestedAmount(investmentPortfolioRequest);
            });
            investmentRepository.saveAll(investments);
        } catch (Exception e) {
            log.error("Failed to update investment dates for groupId: {}", groupId, e);
            throw new RuntimeException("업데이트 실패: " + e.getMessage());
        }
    }

    // 현숙언니가 대출군에서 필요금액 모집 마감 확인하고 불러줘야 하는 api : 투자정산비율 계산 및 업데이트
    @Transactional
    public List<Investment> updateSettlementRatio(InvestmentSettlementRatioUpdateRequest request) {
        List<Investment> investments = investmentRepository.findByGroupId(request.getGroupId());
        if(investments.isEmpty()){
            throw new IllegalArgumentException("No investments found for groupId: " + request.getGroupId());
        }

        BigDecimal totalAmount = investments.stream()
                .map(Investment::getInvestmentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Investment> updatedInvestments = investments.stream()
                .map(investment -> {
                    BigDecimal settlementRatio = investment.getInvestmentAmount()
                            .divide(totalAmount, 8, RoundingMode.HALF_UP);
                    investment.setSettlementRatio(settlementRatio);
                    return investment;
                })
                .collect(Collectors.toList());

        List<Investment> saved = investmentRepository.saveAll(updatedInvestments);
        investmentRepository.flush(); // 여기서 flush
        return saved;
    }


    // 정산금 입금 실행 api : api 확인 필요 - 상환서비스가 호출할 api
    @Transactional
    public void depositSettlementAmount(InvestmentSettlementDepositRequest request) {
        List<Investment> investments = investmentRepository.findByGroupId(request.getGroupId());
        if(investments.isEmpty()){
            throw new IllegalArgumentException("No investments found for groupId: " + request.getGroupId());
        }

        BigDecimal repaymentPrincipal = request.getRepaymentPrincipal();
        BigDecimal repaymentInterest = request.getRepaymentInterest();
        BigDecimal number1 = new BigDecimal("1");
        BigDecimal feeRatio = number1.subtract(BILLIT_CHARGE);

        investments.forEach(investment -> {
            BigDecimal settlementRatio = investment.getSettlementRatio();

            BigDecimal settlementPrincipal = repaymentPrincipal
                    .multiply(settlementRatio)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal settlementProfit = repaymentInterest
                    .multiply(settlementRatio)
                    .multiply(feeRatio)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal settlementAmount = settlementPrincipal.add(settlementProfit);

            try {
                // 투자자 계좌 입금 API 호출
                UserServiceRequestDto depositRequest = new UserServiceRequestDto(
                        investment.getAccountInvestorId(),
                        settlementAmount,
                        "투자 정산금 입금"
                );

               try {
                   userServiceClient.depositToAccount(Long.valueOf(investment.getUserInvestorId()), depositRequest);
               } catch (Exception e) {
                   throw new RuntimeException("정산금 입금 실패");
               }

               settlementService.createSettlement(SettlementCreateRequest.builder()
                       .investmentId(investment.getInvestmentId())
                       .settlementPrincipal(settlementPrincipal)
                       .settlementProfit(settlementProfit)
                       .build());

               // 실제 수익률 생성 및 계산
               InvestmentActualReturnRateCreateRequest investmentActualReturnRateCreateRequest = new InvestmentActualReturnRateCreateRequest(investment.getInvestmentId());
               createInvestmentActualReturnRate(investmentActualReturnRateCreateRequest);

               InvestmentPortfolioRequest investmentPortfolioRequest = new InvestmentPortfolioRequest();
               investmentPortfolioRequest.setUserInvestorId(investment.getUserInvestorId());
               investmentPortfolioService.updateInvestmentPortfolioTotalReturnValueTotalReturnRate(investmentPortfolioRequest);
            } catch (Exception e) {
                // 2-4. 예외 발생 시 상세 로그 기록
                logger.error("Error during deposit for Investment ID: {}, User Investor ID: {}, Amount: {}. Error: {}",
                        investment.getInvestmentId(), investment.getUserInvestorId(), settlementAmount, e.getMessage(), e);
                throw new RuntimeException("Error during deposit for Investment ID: " + investment.getInvestmentId(), e);
            }
        });
    }

    // 현숙언니가 대출군에서 필요금액 모집 마감 확인하고 불러줘야 하는 api : 잔여 투자금 분배 : api 확인 필요
    @Transactional
    public void refundInvestmentBalance(RefundInvestBalanceRequest request) {
        List<Investment> investments = investmentRepository.findByGroupId(request.getGroupId());

        investments.forEach(investment -> {
            BigDecimal settlementRatio = investment.getSettlementRatio();
            BigDecimal depositAmount = request.getRemainingAmount().multiply(settlementRatio);

            UserServiceRequestDto refundRequest = new UserServiceRequestDto(
                    investment.getAccountInvestorId(),
                    depositAmount,
                    "차액 입금"
            );

            // 투자자 계좌 입금 API 호출 : 확인 필요
            try {
                userServiceClient.depositToAccount(Long.valueOf(investment.getUserInvestorId()), refundRequest);
            } catch (Exception e) {
                throw new RuntimeException("투자금 잔액 입금 실패");
            }
                investment.setInvestmentAmount(investment.getInvestmentAmount().subtract(depositAmount));
                investmentRepository.save(investment);
        });
        investmentRepository.saveAll(investments);
        investmentRepository.flush();
    }

    /* invest_status */
    public InvestStatus createInvestmentStatus(Integer investmentId, InvestStatusType statusType) {
        InvestStatus investStatus = new InvestStatus();
        investStatus.setInvestmentId(investmentId);
        investStatus.setInvestStatusType(statusType);

        return investStatusRepository.save(investStatus);
    }

    public InvestStatus updateInvestmentStatus(Integer investmentId, InvestStatusType statusType) {
        InvestStatus status = investStatusRepository.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Investment ID not found"));
        status.setInvestStatusType(statusType);

        return investStatusRepository.save(status);
    }

    public InvestStatus cancelInvestmentStatus(Integer investmentId) {
        InvestStatus status = investStatusRepository.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Investment ID not found"));
        status.setInvestStatusType(InvestStatusType.valueOf("CANCELLED"));

        return investStatusRepository.save(status);
    }

    public InvestStatus getInvestmentStatusByInvestmentId(Integer investmentId) {
        return investStatusRepository.findByInvestmentId(investmentId);
    }



    /* investment_actual_return_rate */
    @Transactional
    public InvestmentActualReturnRate createInvestmentActualReturnRate(InvestmentActualReturnRateCreateRequest request) {
        Integer investmentId = request.getInvestmentId();
        BigDecimal actualReturnRate = caculateActualReturnRate(investmentId);

        InvestmentActualReturnRate investmentActualReturnRate = new InvestmentActualReturnRate();
        investmentActualReturnRate.setInvestmentId(investmentId);
        investmentActualReturnRate.setActualReturnRate(actualReturnRate);
        investmentActualReturnRate.setCreatedAt(LocalDateTime.now());

        return investmentActualReturnRateRepository.save(investmentActualReturnRate);
    }

    public BigDecimal caculateActualReturnRate(Integer investmentId) {
        SettlementPrincipalAndProfitGetResponse response = settlementService.getTotalSettlementPrincipalAndProfit(investmentId);
        BigDecimal totalSettlementPrincipal = response.getTotalSettlementPrincipal();
        BigDecimal totalSettlementProfit = response.getTotalSettlementProfit();
        BigDecimal actualReturnRate = totalSettlementProfit.divide(totalSettlementPrincipal, 2, RoundingMode.HALF_UP);
        return actualReturnRate;
    }

    public List<InvestmentActualReturnRate> getInvestmentActualReturnRate(){
        return investmentActualReturnRateRepository.findAll();
    }

    public List<InvestmentActualReturnRate> getInvestmentActualReturnRateByInvestmentId(Integer investmentId) {
        return investmentActualReturnRateRepository.findByInvestmentId(investmentId);
    }

    public InvestmentActualReturnRate getLatestInvestmentActualReturnRateByInvestmentId(Integer investmentId) {
        return investmentActualReturnRateRepository.findByInvestmentId(investmentId)
                .stream()
                .findFirst()
                .orElse(null);
    }
}