package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "settlement")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settlementId;

    private Integer investmentId;
    @Column(updatable = false)
    private LocalDateTime settlementDate;
    private BigDecimal settlementPrincipal;
    private BigDecimal settlementProfit;

    @PrePersist
    public void prePersist() {
        settlementDate = LocalDateTime.now();
    }
}




