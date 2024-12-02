package com.billit.investment.dto;

import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class InvestmentWithStatusDTO {
    private Investment investment;
    private InvestStatus investStatus;
}
