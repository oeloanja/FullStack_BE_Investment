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
    private Integer settlementId;

    private Integer settlementTimes;
    private LocalDate settlementDate;
    private BigDecimal settlementPrincipal;
    private BigDecimal settlementProfit;
    private Boolean isCompleted;
}



