package com.billit.investment.dto;

import lombok.Getter;
import lombok.Builder;

import java.math.BigDecimal;

@Getter
@Builder
public class SettlementCreateRequest {
    private Integer investmentId;
    private BigDecimal settlementPrincipal;
    private BigDecimal settlementProfit;
}
