package com.billit.investment.controller;

import com.billit.investment.domain.InvestStatus;
import com.billit.investment.domain.InvestStatusType;
import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestmentActualReturnRate;
import com.billit.investment.dto.*;
import com.billit.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invest-service/investments")
@RequiredArgsConstructor
public class InvestmentController {
    private final InvestmentService investmentService;

    /* investment */
    @PostMapping("/create")
    public ResponseEntity<List<Investment>> createInvestment(@RequestBody List<InvestmentCreateRequest> requests) {
        List<Investment> investments = investmentService.createInvestments(requests);
        return ResponseEntity.ok(investments);
    }

    @GetMapping
    public ResponseEntity<List<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse>> getAllInvestmentWithInvestStatusWithInvestmentActualRate() {
        List<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse> results = investmentService.getAllInvestmentWithInvestStatusWithInvestmentActualRate();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{userInvestorId}")
    public ResponseEntity<List<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse>> getInvestmentWithInvestStatusWithInvestmentActualRateByUserInvestorId(@PathVariable Integer userInvestorId) {
        List<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse> results = investmentService.getInvestmentWithInvestStatusWithInvestmentActualRateByInvestorId(userInvestorId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{investmentId}")
    public ResponseEntity<InvestmentWithInvestStatusWithInvestmentActualRateGetResponse> getInvestmentWithInvestStatusWithInvestmentActualRateByinvestmentId(@PathVariable Integer investmentId) {
        InvestmentWithInvestStatusWithInvestmentActualRateGetResponse result = investmentService.getInvestmentWithInvestStatusWithInvestmentActualRateByinvestmentId(investmentId);
        return ResponseEntity.ok(result);
    }

    // 현숙언니가 부를 api (잔여 투자금 분배) - 수정 필요
    @PutMapping("/updateBalance")
    public ResponseEntity<Void> refundInvestAmount(@RequestBody RefundInvestBalanceRequest request) {
        investmentService.refundInvestmentBalance(request);
        return ResponseEntity.noContent().build();
    }

    // 현숙언니가 부를 api (투자일시 업데이트할 때)
    @PutMapping("/group/{groupId}/updateInvestmentDate")
    public ResponseEntity<String> updateInvestmentDatesByGroupId(@PathVariable Integer groupId) {
        investmentService.updateInvestmentDatesByGroupId(groupId);
        String response = "success";
        return ResponseEntity.ok(response);
    }

    // 현숙언니가 부를 api (투자정산비율 업데이트할 때)
    @PutMapping("/group/updateSettlementRatio")
    public ResponseEntity<List<Investment>> updateSettlementRatio(@RequestBody InvestmentSettlementRatioUpdateRequest request) {
        List<Investment> updatedInvestments = investmentService.updateSettlementRatio(request);
        return ResponseEntity.ok(updatedInvestments);
    }

    // 상환서비스가 부를 api(depositSettlementAmount)
    @PostMapping("/deposit-settlement")
    public ResponseEntity<String> depositSettlementAmount(@RequestBody InvestmentSettlementDepositRequest request) {
        try {
            investmentService.depositSettlementAmount(request);
            return ResponseEntity.ok("Settlement deposit completed successfully.");
        } catch (IllegalArgumentException e) {
            // 잘못된 groupId 등의 예외 처리
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            // 그 외 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }



    /* investment_actual_return_rate */
    @PostMapping("/invest-rates/create")
    public ResponseEntity<InvestmentActualReturnRate> createInvestmentActualReturnRate(
            @RequestBody InvestmentActualReturnRateCreateRequest request) {
        InvestmentActualReturnRate investmentActualReturnRate = investmentService.createInvestmentActualReturnRate(request);
        return ResponseEntity.ok(investmentActualReturnRate);
    }

    @GetMapping("/invest-rates")
    public ResponseEntity<List<InvestmentActualReturnRate>> getInvestmentActualReturnRate() {
        List<InvestmentActualReturnRate> investmentActualReturnRate = investmentService.getInvestmentActualReturnRate();
        return ResponseEntity.ok(investmentActualReturnRate);
    }

    @GetMapping("/invest-rates/{investmentId}")
    public ResponseEntity<List<InvestmentActualReturnRate>> getInvestmentActualReturnRateByInvestmentId(@PathVariable Integer investmentId) {
        List<InvestmentActualReturnRate> investmentActualReturnRate = investmentService.getInvestmentActualReturnRateByInvestmentId(investmentId);
        return ResponseEntity.ok(investmentActualReturnRate);
    }

    @GetMapping("/invest-rates/{investmentId}/latest")
    public ResponseEntity<InvestmentActualReturnRate> getLatestInvestmentActualReturnRateByInvestmentId(@PathVariable Integer investmentId) {
        InvestmentActualReturnRate investmentActualReturnRate = investmentService.getLatestInvestmentActualReturnRateByInvestmentId(investmentId);
        return ResponseEntity.ok(investmentActualReturnRate);
    }



    /* invest_status */
    @PostMapping("/{investmentId}/status/create")
    public ResponseEntity<InvestStatus> createInvestmentStatus(
            @PathVariable Integer investmentId,
            @RequestParam InvestStatusType statusType) {
        InvestStatus status = investmentService.createInvestmentStatus(investmentId, statusType);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{investmentId}/status")
    public ResponseEntity<InvestStatus> getInvestmentStatus(@PathVariable Integer investmentId) {
        InvestStatus status = investmentService.getInvestmentStatusByInvestmentId(investmentId);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{investmentId}/status/update")
    public ResponseEntity<InvestStatus> updateInvestmentStatus(
            @PathVariable Integer investmentId,
            @RequestParam InvestStatusType statusType) {
        InvestStatus status = investmentService.updateInvestmentStatus(investmentId, statusType);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{investmentId}/status/cancel")
    public ResponseEntity<InvestStatus> cancelInvestment(@PathVariable Integer investmentId) {
        InvestStatus status = investmentService.cancelInvestmentStatus(investmentId);
        return ResponseEntity.ok(status);
    }
}
