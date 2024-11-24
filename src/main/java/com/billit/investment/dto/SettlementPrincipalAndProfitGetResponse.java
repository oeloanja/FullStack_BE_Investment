package com.billit.investment.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SettlementPrincipalAndProfitGetResponse {
    private BigDecimal totalSettlementPrincipal;
    private BigDecimal totalSettlementProfit;
}
