package com.billit.investment.connect.loan_group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class LoanGroupRequestDto {
    Integer groupId;
    BigDecimal amount;
}
