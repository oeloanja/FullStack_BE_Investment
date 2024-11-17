package com.billit.investment.service;

import com.billit.investment.domain.Investment;
import com.billit.investment.dto.InvestmentRequest;
import com.billit.investment.dto.InvestmentResponse;
import com.billit.investment.repository.InvestmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;

    public InvestmentService(InvestmentRepository investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    public InvestmentResponse createInvestment(InvestmentRequest request) {
        Investment investment = new Investment();
        investment.setUserInvestorId(request.getUserInvestorId());
        investment.setInvestmentAmount(request.getInvestmentAmount());
        investment.setInvestmentDate(request.getInvestmentDate());
        investment.setStatus("대기");

        Investment savedInvestment = investmentRepository.save(investment);

        return new InvestmentResponse(savedInvestment.getId(), savedInvestment.getInvestmentAmount(), savedInvestment.getStatus());
    }

    public List<InvestmentResponse> getInvestmentsByUser(Long userId) {
        List<Investment> investments = investmentRepository.findByUserInvestorId(userId);
        return investments.stream()
                .map(investment -> new InvestmentResponse(investment.getId(), investment.getInvestmentAmount(), investment.getStatus()))
                .collect(Collectors.toList());
    }

    public void updateInvestmentStatus(Long investmentId, String status) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("투자 정보를 찾을 수 없습니다."));
        investment.setStatus(status);
        investmentRepository.save(investment);
    }
}
