package com.billit.investment.kafka.consumer;

import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentDateUpdateConsumer {
    private final InvestmentService investmentService;

    @KafkaListener(
            topics = "investment-date-update",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "investmentDateUpdateEventKafkaListenerContainerFactory"
    )
    public void consumeInvestmentDateUpdate(Integer groupId) {
        try {
            log.info("Processing investment date update for groupId: {}", groupId);
            investmentService.updateInvestmentDatesByGroupId(groupId);
            log.info("Successfully processed investment date update for groupId: {}", groupId);
        } catch (Exception e) {
            log.error("Error processing investment date update for groupId: {}", groupId, e);
            throw e;
        }
    }
}
