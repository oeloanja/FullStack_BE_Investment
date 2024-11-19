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

    @Convert(converter = InvestStatusTypeConverter.class)
    private InvestStatusType investStatusType;
}


