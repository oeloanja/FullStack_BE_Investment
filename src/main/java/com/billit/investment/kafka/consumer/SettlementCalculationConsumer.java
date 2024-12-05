package com.billit.investment.kafka.consumer;

import com.billit.investment.dto.InvestmentSettlementRatioUpdateRequest;
import com.billit.investment.kafka.event.SettlementCalculationEvent;
import com.billit.investment.kafka.processor.ExcessRefundProcessor;
import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementCalculationConsumer {
    private final InvestmentService investmentService;
    private final ExcessRefundProcessor excessRefundProcessor;

    @Transactional
    @KafkaListener(
            topics = "settlement-calculation",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "settlementCalculationEventKafkaListenerContainerFactory"
    )
    public void consumeSettlementCalculation(SettlementCalculationEvent event) {
        try {
            log.info("Processing settlement calculation for groupId: {}", event.getGroupId());
            investmentService.updateSettlementRatio(new InvestmentSettlementRatioUpdateRequest(event.getGroupId()));
            excessRefundProcessor.onSettlementRatioCalculated(event.getGroupId());
            log.info("Successfully processed settlement calculation for groupId: {}", event.getGroupId());
        } catch (Exception e) {
            log.error("Error processing settlement calculation for groupId: {}", event.getGroupId(), e);
            throw e;
        }
    }
}
