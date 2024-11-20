package com.billit.investment.service;

import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.domain.Investment;
import com.billit.investment.dto.InvestmentCreateRequest;
import com.billit.investment.repository.InvestStatusRepository;
import com.billit.investment.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final InvestStatusService investStatusService;
    private final RestTemplate restTemplate;

    @Transactional
    public List<Investment> createInvestments(List<InvestmentCreateRequest> requests) {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Investment request list cannot be empty");
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

    public List<Investment> getAllInvestments() {
        return investmentRepository.findAll();
    }

    public List<Investment> getInvestmentsByInvestorId(Integer userInvestorId) {
        return investmentRepository.findByUserInvestorId(userInvestorId);
    }

    public List<Investment> updateInvestmentDatesByGroupId(Integer groupId) {
        List<Investment> investments = investmentRepository.findByGroupId(groupId);
        LocalDateTime nowTime = LocalDateTime.now();

        if (investments.isEmpty()) {
            throw new IllegalArgumentException("No investments found for groupId: " + groupId);
        }

        for (Investment investment : investments) {
            investment.setInvestmentDate(nowTime);
            investStatusService.updateInvestmentStatus(investment.getInvestmentId(), InvestStatusType.valueOf("EXECUTING"));
        }

        return investmentRepository.saveAll(investments);
    }
}