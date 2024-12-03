package com.billit.investment.service;

import com.billit.investment.domain.Settlement;
import com.billit.investment.dto.SettlementCreateRequest;
import com.billit.investment.dto.SettlementPrincipalAndProfitGetResponse;
import com.billit.investment.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;

    public Settlement createSettlement(SettlementCreateRequest request){
        Settlement settlement = new Settlement();
        settlement.setInvestmentId(request.getInvestmentId());
        settlement.setSettlementPrincipal(request.getSettlementPrincipal());
        settlement.setSettlementProfit(request.getSettlementProfit());
        settlementRepository.save(settlement);
        settlementRepository.flush();

        return settlement;
    }

    public SettlementPrincipalAndProfitGetResponse getTotalSettlementPrincipalAndProfit(Integer investmentId){
        List<Settlement> settlements = settlementRepository.findByInvestmentId(investmentId);
        BigDecimal totalSettlementPrincipal = BigDecimal.ZERO;
        BigDecimal totalSettlementProfit = BigDecimal.ZERO;

        for (Settlement settlement : settlements) {
            totalSettlementPrincipal = totalSettlementPrincipal.add(settlement.getSettlementPrincipal());
            totalSettlementProfit = totalSettlementProfit.add(settlement.getSettlementProfit());
        }
        return SettlementPrincipalAndProfitGetResponse.builder()
                .totalSettlementPrincipal(totalSettlementPrincipal)
                .totalSettlementProfit(totalSettlementProfit)
                .build();
    }

    public List<Settlement> getSettlements(){
        return settlementRepository.findAll();
    }

    public List<Settlement> getSettlementsByInvestmentId(Integer investmentId){
        return settlementRepository.findByInvestmentId(investmentId);
    }
}