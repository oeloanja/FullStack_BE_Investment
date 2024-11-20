package com.billit.investment.service;

import com.billit.investment.domain.Settlement;
import com.billit.investment.domain.SettlementDetail;
import com.billit.investment.repository.SettlementDetailRepository;
import com.billit.investment.repository.SettlementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    @Transactional
    public void calculateSettlement(Integer investmentId, BigDecimal repaymentAmount) {
        Settlement settlement = settlementRepository.findByInvestmentId(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found for investmentId: " + investmentId));

        BigDecimal settlementAmount = repaymentAmount.multiply(settlement.getSettlementRatio());
        BigDecimal principal = calculatePrincipal(settlementAmount);
        BigDecimal profit = settlementAmount.subtract(principal);

        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setSettlementId(settlement.getSettlementId());
        settlementDetail.setSettlementTimes(getNextSettlementTimes(settlement.getSettlementId()));
        settlementDetail.setSettlementDate(LocalDate.now());
        settlementDetail.setSettlementPrincipal(principal);
        settlementDetail.setSettlementProfit(profit);
        settlementDetail.setIsCompleted(true);

        settlementDetailRepository.save(settlementDetail);
    }

    private BigDecimal calculatePrincipal(BigDecimal settlementAmount) {
        // 원금 계산 로직 (예제)
        return settlementAmount.multiply(BigDecimal.valueOf(0.9)); // 90%를 원금으로 가정
    }

    private Integer getNextSettlementTimes(Integer settlementId) {
        // 다음 정산 회차 계산
        return settlementDetailRepository.countBySettlementId(settlementId) + 1;
    }
}
