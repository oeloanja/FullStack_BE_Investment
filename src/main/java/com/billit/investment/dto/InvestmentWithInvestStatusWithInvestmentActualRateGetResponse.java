package com.billit.investment.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class InvestmentWithInvestStatusWithInvestmentActualRateGetResponse {
    private Integer investmentId;
    private Integer groupId;
    private UUID userInvestorId;
    private Integer accountInvestorId;
    private BigDecimal investmentAmount;
    private LocalDateTime investmentDate;
    private BigDecimal expectedReturnRate;
    private LocalDateTime createdAt;
    private Integer investStatusType;
    private BigDecimal settlementRatio;
    private BigDecimal actualReturnRate;
}
