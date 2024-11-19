package com.billit.investment.service;

import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestmentActualReturnRate;
import com.billit.investment.dto.InvestmentCreateRequest;
import com.billit.investment.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public List<Investment> createInvestments(List<InvestmentCreateRequest> requests) {
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

    public List<Investment> updateInvestmentDatesByGroupId(Integer groupId) {
        List<Investment> investments = investmentRepository.findByGroupId(groupId);
        LocalDateTime nowTime = LocalDateTime.now();

        if (investments.isEmpty()) {
            throw new IllegalArgumentException("No investments found for groupId: " + groupId);
        }

        for (Investment investment : investments) {
            investment.setInvestmentDate(nowTime);
        }

        return investmentRepository.saveAll(investments);
    }

    public List<Investment> getAllInvestments() {
        return investmentRepository.findAll();
    }

    public List<Investment> getInvestmentsByInvestor(Integer userInvestorId) {
        return investmentRepository.findByUserInvestorId(userInvestorId);
    }

    public boolean validateInvestmentAmount(Integer accountInvestorId, BigDecimal investmentAmount) {
        // 외부 API 호출 (예: User API)
        BigDecimal accountBalance = getAccountBalance(accountInvestorId);
        return accountBalance.compareTo(investmentAmount) >= 0;
    }

    // 계좌 잔액 조회를 위한 외부 API 호출 메서드
    private BigDecimal getAccountBalance(Integer accountInvestorId) {
        // api 연결 전 테스트용
        //return new BigDecimal("100000.00");
        // 실제 코드
        String url = "http://user-service/api/accounts/" + accountInvestorId + "/balance";
        return restTemplate.getForObject(url, BigDecimal.class);
    }

    public Investment updateInvestmentExecutionDate(Integer investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found"));
        investment.setInvestmentDate(LocalDateTime.now());
        return investmentRepository.save(investment);
    }

    public BigDecimal calculateSettlementAmount(Integer groupId) {
        // 대출군 관련 데이터 및 투자 비율 계산 로직 추가
        BigDecimal totalLoanAmount = loanGroupService.getTotalLoanAmount(groupId);
        List<Investment> investments = investmentRepository.findByGroupId(groupId);

        BigDecimal totalInvested = investments.stream()
                .map(Investment::getInvestmentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return investments.stream()
                .map(investment -> investment.getInvestmentAmount()
                        .divide(totalInvested, 2, RoundingMode.HALF_UP)
                        .multiply(totalLoanAmount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 수익률 계산, 삽입
    public InvestmentActualReturnRate createActualReturnRate(Integer investmentId, BigDecimal actualReturnRate) {
        InvestmentActualReturnRate rate = new InvestmentActualReturnRate();
        rate.setInvestmentId(investmentId);
        rate.setActualReturnRate(actualReturnRate);
        return InvestmentActualReturnRateRepository.save(rate);
    }

    public Optional<InvestmentActualReturnRate> getActualReturnRate(Integer investmentId) {
        return InvestmentActualReturnRateRepository.findByInvestmentId(investmentId);
    }

    public InvestmentActualReturnRate updateActualReturnRate(Integer investmentId, BigDecimal newRate) {
        InvestmentActualReturnRate rate = InvestmentActualReturnRateRepository.findByInvestmentId(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Actual return rate not found for investment ID: " + investmentId));
        rate.setActualReturnRate(newRate);
        return InvestmentActualReturnRateRepository.save(rate);
    }

    public boolean actualReturnRateExists(Integer investmentId) {
        return InvestmentActualReturnRateRepository.existsByInvestmentId(investmentId);
    }
}
