package com.billit.investment.dto;
import java.math.BigDecimal;

public class PortfolioResponse {
    private Long portfolioId;
    private String portfolioName;
    private BigDecimal totalInvestedAmount;
    private String riskLevel;

    public PortfolioResponse(Long portfolioId, String portfolioName, BigDecimal totalInvestedAmount, String riskLevel) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.totalInvestedAmount = totalInvestedAmount;
        this.riskLevel = riskLevel;
    }

    // Getters and Setters
    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getTotalInvestedAmount() {
        return totalInvestedAmount;
    }

    public void setTotalInvestedAmount(BigDecimal totalInvestedAmount) {
        this.totalInvestedAmount = totalInvestedAmount;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
