package com.billit.investment.connect.loan_group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class LoanGroupRequestDto {
    Integer groupId;
    BigDecimal amount;

    public LoanGroupRequestDto(Integer groupId, BigDecimal amount) {
        this.groupId = groupId;
        this.amount = amount;
    }
}
