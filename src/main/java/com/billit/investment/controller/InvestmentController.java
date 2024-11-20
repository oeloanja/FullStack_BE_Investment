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

    @PostMapping("/create")
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
    public ResponseEntity<List<Investment>> getInvestmentsByInvestorId(@PathVariable Integer userInvestorId) {
        List<Investment> investments = investmentService.getInvestmentsByInvestorId(userInvestorId);
        return ResponseEntity.ok(investments);
    }

    // 현숙언니 잔여 투자금을 찢어서 돌려줄 때 사용할 api - 매개변수 : group_id, 잔여금액 - 잔여금액이 0 초과면 흩뿌리고(민석오빠의 api를 호출) 0 이면 아무것도 안함
    @PutMapping("/updateBalance")
    public ResponseEntity<Void> refundInvestAmount() {
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/group/{groupId}/investmentDate")
    public ResponseEntity<List<Investment>> updateInvestmentDatesByGroupId(@PathVariable Integer groupId) {
        List<Investment> updatedInvestments = investmentService.updateInvestmentDatesByGroupId(groupId);
        return ResponseEntity.ok(updatedInvestments);
    }

    @PostMapping("/{investmentId}/status/create")
    public ResponseEntity<InvestStatus> createInvestmentStatus(
            @PathVariable Integer investmentId,
            @RequestParam InvestStatusType statusType) {
        InvestStatus status = investStatusService.createInvestmentStatus(investmentId, statusType);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{investmentId}/status")
    public ResponseEntity<InvestStatus> getInvestmentStatus(@PathVariable Integer investmentId) {
        InvestStatus status = investStatusService.getInvestmentStatusByInvestmentId(investmentId);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{investmentId}/status/update")
    public ResponseEntity<InvestStatus> updateInvestmentStatus(
            @PathVariable Integer investmentId,
            @RequestParam InvestStatusType statusType) {
        InvestStatus status = investStatusService.updateInvestmentStatus(investmentId, statusType);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{investmentId}/status/cancel")
    public ResponseEntity<InvestStatus> cancelInvestment(@PathVariable Integer investmentId) {
        InvestStatus status = investStatusService.cancelInvestmentStatus(investmentId);
        return ResponseEntity.ok(status);
    }
}
