package com.billit.investment.controller;

import com.billit.investment.domain.InvestmentPortfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {
    //투자고유번호를 전달해서 생성하기
    //투자고유번호로 정산찾기
    //정산고유번호 전달해서 생성하기
    //정산고유번호로 정산상세 찾기
    @PostMapping("/create")
    public ResponseEntity<InvestmentPortfolio> createSettlement(@RequestBody InvestmentPortfolioCreateRequest request) {
        return ResponseEntity.ok(investmentPortfolioService.createInvestmentPortfolio(request));
    }

    @GetMapping
    public ResponseEntity<List<InvestmentPortfolio>> getAllInvestmentPortfolios() {
        return ResponseEntity.ok(investmentPortfolioService.getAllPortfolios());
    }

    @GetMapping("/{userInvestorId}")
    public ResponseEntity<List<InvestmentPortfolio>> getInvestmentPortfoliosByUser(@PathVariable Integer userInvestorId) {
        return ResponseEntity.ok(investmentPortfolioService.getPortfoliosByUser(userInvestorId));
    }

    @PutMapping("/{portfolioId}/update")
    public ResponseEntity<InvestmentPortfolio> updateInvestmentPortfolio(
            @PathVariable Integer portfolioId,
            @RequestBody InvestmentPortfolioCreateRequest request) {
        return ResponseEntity.ok(investmentPortfolioService.updatePortfolio(portfolioId, request));
    }
}
