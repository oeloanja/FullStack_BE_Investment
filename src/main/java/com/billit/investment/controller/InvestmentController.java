package com.billit.investment.controller;

import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.domain.Investment;
import com.billit.investment.dto.InvestmentCreateRequest;
import com.billit.investment.service.InvestStatusService;
import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
public class InvestmentController {
    private final InvestmentService investmentService;
    private final InvestStatusService investStatusService;

    @PostMapping
    public ResponseEntity<List<Investment>> createInvestment(@RequestBody List<InvestmentCreateRequest> requests) {
        List<Investment> investments = investmentService.createInvestments(requests);
        return ResponseEntity.ok(investments);
    }

    @GetMapping
    public ResponseEntity<List<Investment>> getAllInvestments() {
        List<Investment> investments = investmentService.getAllInvestments();
        return ResponseEntity.ok(investments);
    }

    @GetMapping("/{userInvestorId}")
    public ResponseEntity<List<Investment>> getInvestmentsByInvestor(@PathVariable Integer userInvestorId) {
        List<Investment> investments = investmentService.getInvestmentsByInvestor(userInvestorId);
        return ResponseEntity.ok(investments);
    }

    // 현숙언니가 부를 api
    @PutMapping("/updateBalance")
    public ResponseEntity<Void> updateBalance(@RequestBody InvestmentCreateRequest request) {
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/group/{groupId}/investmentDate")
    public ResponseEntity<List<Investment>> updateInvestmentDatesByGroupId(@PathVariable Integer groupId) {
        List<Investment> updatedInvestments = investmentService.updateInvestmentDatesByGroupId(groupId);
        return ResponseEntity.ok(updatedInvestments);
    }

    @PostMapping("/{investmentId}/status")
    public ResponseEntity<InvestStatus> updateInvestmentStatus(
            @PathVariable Integer investmentId,
            @RequestParam InvestStatusType statusType) {
        InvestStatus status = investStatusService.updateInvestmentStatus(investmentId, statusType);
        return ResponseEntity.ok(status);
    }
}
