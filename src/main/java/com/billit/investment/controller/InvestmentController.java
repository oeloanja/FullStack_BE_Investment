package com.billit.investment.controller;

import com.billit.investment.dto.InvestmentRequest;
import com.billit.investment.dto.InvestmentResponse;
import com.billit.investment.service.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping
    public ResponseEntity<InvestmentResponse> createInvestment(@RequestBody InvestmentRequest request) {
        InvestmentResponse response = investmentService.createInvestment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<InvestmentResponse>> getInvestmentsByUser(@PathVariable Long userId) {
        List<InvestmentResponse> investments = investmentService.getInvestmentsByUser(userId);
        return ResponseEntity.ok(investments);
    }

    @PatchMapping("/{investmentId}/status")
    public ResponseEntity<Void> updateInvestmentStatus(@PathVariable Long investmentId, @RequestParam String status) {
        investmentService.updateInvestmentStatus(investmentId, status);
        return ResponseEntity.noContent().build();
    }
}
