package com.billit.investment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_portfolios")
public class InvestmentPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 포트폴리오 고유번호

    @Column(name = "user_investor_id", nullable = false)
    private Long userInvestorId;  // 투자자 고유 번호

    @Column(name = "portfolio_name", length = 100, nullable = false)
    private String portfolioName;  // 포트폴리오 이름

    @Column(name = "risk_level", length = 20)
    private String riskLevel;  // 위험도 (예: "LOW", "MEDIUM", "HIGH")

    @Column(name = "target_return_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal targetReturnRate;  // 목표 수익률

    @Column(name = "total_invested_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalInvestedAmount;  // 총 투자 금액

    @Column(name = "actual_return_value", precision = 15, scale = 2)
    private BigDecimal actualReturnValue;  // 실제 수익 금액

    @Column(name = "actual_return_rate", precision = 5, scale = 2)
    private BigDecimal actualReturnRate;  // 실제 수익률

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 포트폴리오 생성일시

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserInvestorId() {
        return userInvestorId;
    }

    public void setUserInvestorId(Long userInvestorId) {
        this.userInvestorId = userInvestorId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getTargetReturnRate() {
        return targetReturnRate;
    }

    public void setTargetReturnRate(BigDecimal targetReturnRate) {
        this.targetReturnRate = targetReturnRate;
    }

    public BigDecimal getTotalInvestedAmount() {
        return totalInvestedAmount;
    }

    public void setTotalInvestedAmount(BigDecimal totalInvestedAmount) {
        this.totalInvestedAmount = totalInvestedAmount;
    }

    public BigDecimal getActualReturnValue() {
        return actualReturnValue;
    }

    public void setActualReturnValue(BigDecimal actualReturnValue) {
        this.actualReturnValue = actualReturnValue;
    }

    public BigDecimal getActualReturnRate() {
        return actualReturnRate;
    }

    public void setActualReturnRate(BigDecimal actualReturnRate) {
        this.actualReturnRate = actualReturnRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

