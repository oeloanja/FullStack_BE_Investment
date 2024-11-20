package com.billit.investment.service;

import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.repository.InvestStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvestStatusService {
    private final InvestStatusRepository investStatusRepository;

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
}