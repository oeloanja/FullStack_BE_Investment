package com.billit.investment.kafka.compensation.handler;

import com.billit.investment.connect.user.client.UserServiceClient;
import com.billit.investment.connect.user.dto.UserServiceRequestDto;
import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.domain.Investment;
import com.billit.investment.kafka.compensation.event.InvestmentDateCompensationEvent;
import com.billit.investment.kafka.compensation.event.LastInvestmentRefundEvent;
import com.billit.investment.repository.InvestStatusRepository;
import com.billit.investment.repository.InvestmentRepository;
import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentCompensationHandler {
    private final InvestmentRepository investmentRepository;
    private final InvestStatusRepository investStatusRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final InvestmentService investmentService;

    @KafkaListener(topics = "investment-date-compensation")
    public void handleInvestmentDateCompensation(InvestmentDateCompensationEvent event) {
        try {
            List<Investment> investments = investmentRepository.findByGroupId(event.getGroupId());
            investments.forEach(investment -> {
                investment.setInvestmentDate(null);

                InvestStatus status = investStatusRepository.findById(investment.getInvestmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Investment status not found"));
                status.setInvestStatusType(InvestStatusType.WAITING);

                investStatusRepository.save(status);
            });
            investmentRepository.saveAll(investments);
            log.info("Successfully reverted investment dates for groupId: {}", event.getGroupId());
        } catch (Exception e) {
            log.error("Failed to revert investment dates", e);
            throw e;
        }
    }

    @KafkaListener(topics = "last-investment-refund")
    public void handleLastInvestmentRefund(LastInvestmentRefundEvent event) {
        try {
            Investment lastInvestment = investmentRepository
                    .findTopByGroupIdOrderByCreatedAtDesc(event.getGroupId())
                    .orElseThrow();

            // 환급 이벤트 발행
            userServiceClient.depositToAccount(lastInvestment.getUserInvestorId(),
                    new UserServiceRequestDto(lastInvestment.getAccountInvestorId(),
                            lastInvestment.getInvestmentAmount(),
                            "투자금 환불")
            );

            // 투자 금액 업데이트
            lastInvestment.setInvestmentAmount(
                    lastInvestment.getInvestmentAmount().subtract(event.getAmount())
            );
            investmentService.updateInvestmentStatus(lastInvestment.getInvestmentId(), InvestStatusType.CANCELED);
            investmentRepository.save(lastInvestment);

            log.info("Successfully refunded last investment for groupId: {}", event.getGroupId());
        } catch (Exception e) {
            log.error("Failed to refund last investment", e);
            throw e;
        }
    }
}
