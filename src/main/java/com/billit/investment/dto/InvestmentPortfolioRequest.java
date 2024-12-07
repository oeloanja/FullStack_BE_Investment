package com.billit.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InvestmentPortfolioRequest {
    UUID userInvestorId;
}
