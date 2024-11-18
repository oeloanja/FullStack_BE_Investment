package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvestmentResponse {
    private Integer investmentId;
    private Integer groupId;
    private Integer userInvestorId;
    private BigDecimal investmentAmount;
    private BigDecimal expectedReturnRate;
    private LocalDateTime createdAt;
}

