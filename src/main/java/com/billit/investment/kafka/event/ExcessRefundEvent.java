package com.billit.investment.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcessRefundEvent {
    private Integer groupId;
    private BigDecimal excess;
    private String status;
}