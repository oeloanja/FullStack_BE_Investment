package com.billit.investment.kafka.processor;

import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExcessRefundProcessor {
    private final Map<Integer, CompletableFuture<Void>> pendingRefunds = new ConcurrentHashMap<>();
    private final InvestmentService investmentService;

    public void onSettlementRatioCalculated(Integer groupId) {
        CompletableFuture<Void> pendingRefund = pendingRefunds.remove(groupId);
        if (pendingRefund != null) {
            pendingRefund.complete(null);
        }
    }

    public CompletableFuture<Void> waitForSettlementRatio(Integer groupId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        pendingRefunds.put(groupId, future);
        return future;
    }
}
