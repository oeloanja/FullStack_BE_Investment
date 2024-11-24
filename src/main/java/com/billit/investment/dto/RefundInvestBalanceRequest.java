package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundInvestBalanceRequest {
    private Integer groupId;
    private BigDecimal remainingAmount;
}
