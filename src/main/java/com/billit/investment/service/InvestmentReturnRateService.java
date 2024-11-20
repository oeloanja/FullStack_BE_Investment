package com.billit.investment.service;

import com.billit.investment.domain.InvestmentActualReturnRate;
import com.billit.investment.repository.InvestmentActualReturnRateRepository;
import com.billit.investment.repository.InvestmentRepository;
import com.billit.investment.repository.SettlementDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvestmentReturnRateService {
    private final SettlementDetailRepository settlementDetailRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentActualReturnRateRepository actualReturnRateRepository;

    @Transactional
    public void updateActualReturnRate(Integer investmentId) {
        BigDecimal totalProfit = settlementDetailRepository.findTotalProfitBySettlementId(investmentId);
        BigDecimal investmentAmount = investmentRepository.findInvestmentAmountByInvestmentId(investmentId);

        BigDecimal actualReturnRate = totalProfit.divide(investmentAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        InvestmentActualReturnRate returnRate = actualReturnRateRepository.findByInvestmentId(investmentId)
                .orElse(new InvestmentActualReturnRate());

        returnRate.setInvestmentId(investmentId);
        returnRate.setActualReturnRate(actualReturnRate);
        returnRate.setCreatedAt(LocalDateTime.now());

        actualReturnRateRepository.save(returnRate);
    }
}
