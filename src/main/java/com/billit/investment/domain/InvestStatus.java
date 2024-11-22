package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class InvestStatus {
    @Id
    private Integer investmentId;

    @Convert(converter = InvestStatusTypeConverter.class)
    private InvestStatusType investStatusType;
}


