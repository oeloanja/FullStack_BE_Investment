package com.billit.investment.controller;

import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.dto.InvestmentPortfolioRequest;
import com.billit.investment.service.InvestmentPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invest-service/portfolios")
@RequiredArgsConstructor
public class InvestmentPortfolioController {
    private final InvestmentPortfolioService investmentPortfolioService;

    @PostMapping("/create")
    public ResponseEntity<InvestmentPortfolio> createInvestmentPortfolio(@RequestBody InvestmentPortfolioRequest request) {
        return ResponseEntity.ok(investmentPortfolioService.createInvestmentPortfolio(request));
    }

    @GetMapping
    public ResponseEntity<List<InvestmentPortfolio>> getAllInvestmentPortfolios() {
        return ResponseEntity.ok(investmentPortfolioService.getAllPortfolios());
    }

    @GetMapping("/exist")
    public boolean isExistPortfolio(@RequestBody InvestmentPortfolioRequest request){
        return investmentPortfolioService.isExistPortfolio(request);
    }

    @GetMapping("/{userInvestorId}")
    public ResponseEntity<InvestmentPortfolio> getInvestmentPortfoliosByUser(@PathVariable UUID userInvestorId) {
        return ResponseEntity.ok(investmentPortfolioService.getPortfoliosByUserInvestorId(userInvestorId));
    }

    @PutMapping("/updatePortfolioTotalInvestedAmount")
    public ResponseEntity<InvestmentPortfolio> updatePortfolioTotalInvestedAmount(
            @RequestBody InvestmentPortfolioRequest request) {
        return ResponseEntity.ok(investmentPortfolioService.updatePortfolioTotalInvestedAmount(request));
    }

    @PutMapping("/updateInvestmentPortfolioTotalReturnValueTotalReturnRate")
    public ResponseEntity<InvestmentPortfolio> updateInvestmentPortfolioTotalReturnValueTotalReturnRate(
            @RequestBody InvestmentPortfolioRequest request) {
        return ResponseEntity.ok(investmentPortfolioService.updateInvestmentPortfolioTotalReturnValueTotalReturnRate(request));
    }
}

