package com.billit.investment.connect.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceRequestDto {
    private Integer accountId;
    private BigDecimal amount;
    private String description;
}
