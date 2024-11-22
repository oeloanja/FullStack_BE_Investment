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
public class InvestmentActualReturnRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer investmentReturnRateId;

    private Integer investmentId;
    private BigDecimal actualReturnRate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

