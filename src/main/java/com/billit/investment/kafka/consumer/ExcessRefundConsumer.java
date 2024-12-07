package com.billit.investment.kafka.consumer;

import com.billit.investment.dto.RefundInvestBalanceRequest;
import com.billit.investment.kafka.event.ExcessRefundEvent;
import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcessRefundConsumer {
    private final InvestmentService investmentService;

    @KafkaListener(
            topics = "excess-refund",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "excessRefundEventKafkaListenerContainerFactory"
    )
    public void consumeExcessRefund(ExcessRefundEvent event) {
        try {
            log.info("Processing excess refund for groupId: {}", event.getGroupId());

            investmentService.refundInvestmentBalance(
                    new RefundInvestBalanceRequest(event.getGroupId(), event.getExcess())
            );
            log.info("Successfully processed excess refund for groupId: {}", event.getGroupId());

        } catch (Exception e) {
            log.error("Error processing excess refund for groupId: {}", event.getGroupId(), e);
            throw e;
        }
    }
}