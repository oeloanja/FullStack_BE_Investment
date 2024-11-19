package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvestmentPortfolioRequest {
    private Integer userInvestorId;
    private String portfolioName;
    private String riskLevel;
    private BigDecimal targetReturnRate;
    private BigDecimal totalInvestedAmount;
    private BigDecimal actualReturnValue;
    private BigDecimal actualReturnRate;
}
