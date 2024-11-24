package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvestmentSettlementDepositRequest {
    private Integer groupId;
    private BigDecimal repaymentPrincipal;
    private BigDecimal repaymentInterest;
}
