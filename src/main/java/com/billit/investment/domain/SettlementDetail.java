package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "settlement_detail")
public class SettlementDetail {
    @Id
    @Column(nullable = false)
    private Integer settlementId;

    @Column(nullable = false)
    private Integer settlementTimes;

    @Column(nullable = false)
    private LocalDate settlementDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal settlementPrincipal;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal settlementProfit;

    @Column(nullable = false)
    private Boolean isCompleted;
}



