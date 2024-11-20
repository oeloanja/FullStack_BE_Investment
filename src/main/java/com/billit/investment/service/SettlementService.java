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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    //정산 생성

    //투자고유번호로 정산 찾기(레파지토리 사용)

    //정산고유번호로 정산상세 찾기

    //투자고유번호로 정산고유번호 찾기
    public Integer getSettlementId(Integer investmentId) {
        return settlementRepository.findById(investmentId).get().getSettlementId();
    }

    public BigDecimal getTotalSettlementPrincipal(Integer settlementId){
        List<SettlementDetail> settlementDetails = settlementDetailRepository.findBySettlementId(settlementId);
        BigDecimal totalSettlementPrincipal = BigDecimal.ZERO;
        for (SettlementDetail settlementDetail : settlementDetails) {
            totalSettlementPrincipal = totalSettlementPrincipal.add(settlementDetail.getSettlementPrincipal());
        }
        return totalSettlementPrincipal;
    }

    public BigDecimal getTotalSettlementProfit(Integer settlementId){
        List<SettlementDetail> settlementDetails = settlementDetailRepository.findBySettlementId(settlementId);
        BigDecimal totalSettlementProfit = BigDecimal.ZERO;
        for (SettlementDetail settlementDetail : settlementDetails) {
            totalSettlementProfit = totalSettlementProfit.add(settlementDetail.getSettlementProfit());
        }
        return totalSettlementProfit;
    }

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