package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class InvestStatus {
    @Id
    private Integer investmentId;

    @Enumerated(EnumType.STRING)
    private InvestStatusType investStatusType;

    public enum InvestStatusType {
        PENDING, COMPLETED, CANCELLED
    }
}


