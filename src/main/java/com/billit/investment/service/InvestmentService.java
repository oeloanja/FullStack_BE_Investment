package com.billit.investment.service;

import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestmentActualReturnRate;
import com.billit.investment.dto.*;
import com.billit.investment.repository.InvestStatusRepository;
import com.billit.investment.repository.InvestmentActualReturnRateRepository;
import com.billit.investment.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final InvestStatusRepository investStatusRepository;
    private final InvestmentActualReturnRateRepository investmentActualReturnRateRepository;
    private final RestTemplate restTemplate;
    private final SettlementService settlementService;

    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    private final BigDecimal BILLIT_CHARGE = new BigDecimal("0.5");

    @Transactional
    public List<Investment> createInvestments(List<InvestmentCreateRequest> requests) {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Investment request list is empty");
        }

        Integer accountInvestorId = requests.get(0).getAccountInvestorId();
        BigDecimal totalInvestmentAmount = requests.stream()
                .map(InvestmentCreateRequest::getInvestmentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 0단계: 투자자 계좌 잔액 확인
        if (!validateInvestmentAmount(accountInvestorId, totalInvestmentAmount)) {
            throw new IllegalStateException("Insufficient account balance for investment");
        }

        // 1단계: 투자금 출금
        withdrawInvestmentAmount(accountInvestorId, totalInvestmentAmount);

        // 2단계: 출금 확인
        confirmWithdrawal(accountInvestorId, totalInvestmentAmount);

        // 3단계: LoanGroupService에 투자금 입금
        Integer groupId = requests.get(0).getGroupId();
        depositToLoanGroup(groupId, totalInvestmentAmount);

        // 4단계: 입금 확인
        confirmDeposit(groupId, totalInvestmentAmount);

        // 5단계: 투자 정보 저장
        List<Investment> investments = requests.stream().map(request -> {
            Investment investment = new Investment();
            investment.setGroupId(request.getGroupId());
            investment.setUserInvestorId(request.getUserInvestorId());
            investment.setAccountInvestorId(request.getAccountInvestorId());
            investment.setInvestmentAmount(request.getInvestmentAmount());
            investment.setExpectedReturnRate(request.getExpectedReturnRate());
            return investment;
        }).collect(Collectors.toList());

        return investmentRepository.saveAll(investments);
    }

    // api 호출 부분의 경우, 상대의 api를 확인하기
    private boolean validateInvestmentAmount(Integer accountInvestorId, BigDecimal totalInvestmentAmount) {
        String url = "http://user-service/api/accounts/" + accountInvestorId + "/balance";
        BigDecimal accountBalance = restTemplate.getForObject(url, BigDecimal.class);
        return accountBalance.compareTo(totalInvestmentAmount) >= 0;
    }

    private void withdrawInvestmentAmount(Integer accountInvestorId, BigDecimal amount) {
        String url = "http://user-service/api/accounts/" + accountInvestorId + "/withdraw";
        restTemplate.postForObject(url, amount, Void.class);
    }

    private void confirmWithdrawal(Integer accountInvestorId, BigDecimal amount) {
        // 잔액을 다시 확인하여 출금 확인
        String url = "http://user-service/api/accounts/" + accountInvestorId + "/balance";
        BigDecimal accountBalance = restTemplate.getForObject(url, BigDecimal.class);
        if (accountBalance.compareTo(amount) >= 0) {
            throw new IllegalStateException("Withdrawal was not successful for amount: " + amount);
        }
    }

    private void depositToLoanGroup(Integer groupId, BigDecimal amount) {
        String url = "http://loan-group-service/api/groups/" + groupId + "/deposit";
        restTemplate.postForObject(url, amount, Void.class);
    }

    private void confirmDeposit(Integer groupId, BigDecimal amount) {
        // LoanGroupService의 입금 확인 API 호출
        String url = "http://loan-group-service/api/groups/" + groupId + "/balance";
        BigDecimal groupBalance = restTemplate.getForObject(url, BigDecimal.class);
        if (groupBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Deposit was not successful for amount: " + amount);
        }
    }

    public List<InvestmentWithInvestStatusGetResponse> getAllInvestmentWithInvestStatus() {
        List<Object[]> results = investmentRepository.findInvestmentWithStatus();
        return results.stream()
                .map(result -> {
                    Investment investment = (Investment) result[0];
                    InvestStatus investStatus = (InvestStatus) result[1];

                    return InvestmentWithInvestStatusGetResponse.builder()
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
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<InvestmentWithInvestStatusGetResponse> getInvestmentWithInvestStatusByInvestorId(Integer userInvestorId) {
        List<Object[]> results = investmentRepository.findInvestmentWithStatusByUserInvestorId(userInvestorId);
        return results.stream()
                .map(result -> {
                    Investment investment = (Investment) result[0];
                    InvestStatus investStatus = (InvestStatus) result[1];

                    return InvestmentWithInvestStatusGetResponse.builder()
                            .investmentId(investment.getInvestmentId())
                            .groupId(investment.getGroupId())
                            .userInvestorId(investment.getUserInvestorId())
                            .accountInvestorId(investment.getAccountInvestorId())
                            .investmentAmount(investment.getInvestmentAmount())
                            .investmentDate(investment.getInvestmentDate())
                            .expectedReturnRate(investment.getExpectedReturnRate())
                            .createdAt(investment.getCreatedAt())
                            .investStatusType(investStatus.getInvestStatusType().getCode())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 현숙언니가 대출군에서 필요금액 모집 마감 확인하고 불러줘야 하는 api : 투자실행일시 업데이트
    @Transactional
    public List<Investment> updateInvestmentDatesByGroupId(Integer groupId) {
        List<Investment> investments = investmentRepository.findByGroupId(groupId);
        LocalDateTime nowTime = LocalDateTime.now();

        if (investments.isEmpty()) {
            throw new IllegalArgumentException("No investments found for groupId: " + groupId);
        }

        for (Investment investment : investments) {
            investment.setInvestmentDate(nowTime);
            updateInvestmentStatus(investment.getInvestmentId(), InvestStatusType.valueOf("EXECUTING"));
        }

        return investmentRepository.saveAll(investments);
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

        return investmentRepository.saveAll(updatedInvestments);
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
                    .multiply(feeRatio)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal settlementProfit = repaymentInterest
                    .multiply(settlementRatio)
                    .multiply(feeRatio)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal settlementAmount = settlementPrincipal.add(settlementProfit);

            try {
                // 투자자 계좌 입금 API 호출
                boolean isDeposited = userService.depositToAccount(investment.getUserInvestorId(), settlementAmount);

                if (isDeposited) {
                    settlementService.createSettlement(SettlementCreateRequest.builder()
                            .investmentId(investment.getInvestmentId())
                            .settlementPrincipal(settlementPrincipal)
                            .settlementProfit(settlementProfit)
                            .build());
                } else {
                    logger.error("Failed to deposit settlement amount for Investment ID: {}, User Investor ID: {}, Amount: {}",
                            investment.getInvestmentId(), investment.getUserInvestorId(), settlementAmount);
                    throw new RuntimeException("Deposit failed for Investment ID: " + investment.getInvestmentId());
                }
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

            // 투자자 계좌 입금 API 호출 : 확인 필요
            boolean isDeposited = userService.depositToAccount(investment.getUserInvestorId(), depositAmount);

            if (isDeposited) {
                investment.setInvestmentAmount(investment.getInvestmentAmount().subtract(depositAmount));
                investmentRepository.save(investment);
            } else {
                throw new RuntimeException("투자금 잔액 입금 실패");
            }
        });
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
                .orElseThrow(() -> new NoSuchElementException("No InvestmentActualReturnRate found!"));
    }
}