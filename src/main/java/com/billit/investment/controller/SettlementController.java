package com.billit.investment.controller;

import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.domain.Settlement;
import com.billit.investment.dto.SettlementCreateRequest;
import com.billit.investment.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    @PostMapping("/create")
    public ResponseEntity<Settlement> createSettlement(@RequestBody SettlementCreateRequest request) {
        return ResponseEntity.ok(settlementService.createSettlement(request));
    }

    @GetMapping
    public ResponseEntity<List<Settlement>> getSettlements() {
        return ResponseEntity.ok(settlementService.getSettlements());
    }

    @GetMapping("/{investmentId}")
    public ResponseEntity<List<Settlement>> getSettlementsByInvestmentId(@PathVariable Integer investmentId) {
        return ResponseEntity.ok(settlementService.getSettlementsByInvestmentId(investmentId));
    }
}
