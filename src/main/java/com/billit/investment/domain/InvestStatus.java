package com.billit.investment.domain;

import jakarta.persistence.*;

@Entity
public class InvestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "investment_id")
    private Investment investment;

    private String statusType; // 예: 대기, 진행 중, 취소됨

    // Getters and Setters
}

