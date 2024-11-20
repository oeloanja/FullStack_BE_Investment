//package com.billit.investment.controller;
//
//import com.billit.investment.domain.InvestmentPortfolio;
//import com.billit.investment.dto.InvestmentPortfolioRequest;
//import com.billit.investment.service.InvestmentPortfolioService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/portfolios")
//@RequiredArgsConstructor
//public class InvestmentPortfolioController {
//    private final InvestmentPortfolioService portfolioService;
//
//    @PostMapping
//    public ResponseEntity<InvestmentPortfolio> createPortfolio(@RequestBody InvestmentPortfolioRequest request) {
//        return ResponseEntity.ok(portfolioService.createPortfolio(request));
//    }
//
//    @PutMapping("/{portfolioId}")
//    public ResponseEntity<InvestmentPortfolio> updatePortfolio(
//            @PathVariable Integer portfolioId,
//            @RequestBody InvestmentPortfolioRequest request) {
//        return ResponseEntity.ok(portfolioService.updatePortfolio(portfolioId, request));
//    }
//
//    @GetMapping("/user/{userInvestorId}")
//    public ResponseEntity<List<InvestmentPortfolio>> getPortfoliosByUser(@PathVariable Integer userInvestorId) {
//        return ResponseEntity.ok(portfolioService.getPortfoliosByUser(userInvestorId));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<InvestmentPortfolio>> getAllPortfolios() {
//        return ResponseEntity.ok(portfolioService.getAllPortfolios());
//    }
//}
//
