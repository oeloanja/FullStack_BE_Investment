package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "settlement")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settlementId;

    @Column(nullable = false)
    private Integer investmentId;

    @Column(nullable = false, precision = 10, scale = 8)
    private Double settlementRatio;
}



