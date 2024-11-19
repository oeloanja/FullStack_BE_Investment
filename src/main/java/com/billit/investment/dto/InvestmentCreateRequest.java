package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvestmentCreateRequest {
    private Integer groupId;
    private Integer userInvestorId;
    private Integer accountInvestorId;
    private BigDecimal investmentAmount;
    private BigDecimal expectedReturnRate;
}

