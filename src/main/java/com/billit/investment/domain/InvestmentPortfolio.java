package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class InvestmentPortfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portfolioId;

    private Integer userInvestorId;
    private BigDecimal totalInvestedAmount;
    private BigDecimal totalReturnValue;
    private BigDecimal totalReturnRate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}