package com.billit.investment.dto;

import java.math.BigDecimal;

public class InvestmentResponse {
    private Long investmentId;
    private BigDecimal investmentAmount;
    private String status;

    public InvestmentResponse(Long investmentId, BigDecimal investmentAmount, String status) {
        this.investmentId = investmentId;
        this.investmentAmount = investmentAmount;
        this.status = status;
    }

    // Getters and Setters

    public Long getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }

    public BigDecimal getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

