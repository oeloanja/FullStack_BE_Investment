package com.billit.investment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvestmentRequest {
    private Long userInvestorId;
    private BigDecimal investmentAmount;
    private LocalDateTime investmentDate;

    // Getters and Setters

    public Long getUserInvestorId() {
        return userInvestorId;
    }

    public void setUserInvestorId(Long userInvestorId) {
        this.userInvestorId = userInvestorId;
    }

    public BigDecimal getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public LocalDateTime getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(LocalDateTime investmentDate) {
        this.investmentDate = investmentDate;
    }
}
