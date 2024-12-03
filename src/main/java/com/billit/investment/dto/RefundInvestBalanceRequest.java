package com.billit.investment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class RefundInvestBalanceRequest {
    private Integer groupId;
    private BigDecimal remainingAmount;
}
