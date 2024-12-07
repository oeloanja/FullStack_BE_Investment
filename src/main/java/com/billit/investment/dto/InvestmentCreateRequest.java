package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class InvestmentCreateRequest {
    private Integer groupId;
    private UUID userInvestorId;
    private Integer accountInvestorId;
    private BigDecimal investmentAmount;
    private BigDecimal expectedReturnRate;
}

