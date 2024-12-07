package com.billit.investment.service;

import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.dto.InvestmentPortfolioRequest;
import com.billit.investment.dto.SettlementPrincipalAndProfitGetResponse;
import com.billit.investment.repository.InvestmentPortfolioRepository;
import com.billit.investment.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestmentPortfolioService {
    private final InvestmentRepository investmentRepository;
    private final InvestmentPortfolioRepository investmentPortfolioRepository;
    private final SettlementService settlementService;

    @Transactional
    public InvestmentPortfolio createInvestmentPortfolio(InvestmentPortfolioRequest request) {
        UUID userInvestorId = request.getUserInvestorId();
        List<Investment> investments = investmentRepository.findByUserInvestorId(userInvestorId);

        BigDecimal totalInvestedAmount = BigDecimal.ZERO;
        BigDecimal totalReturnValue = BigDecimal.ZERO;
        BigDecimal totalReturnRate = BigDecimal.ZERO;

        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        portfolio.setUserInvestorId(userInvestorId);
        portfolio.setTotalInvestedAmount(totalInvestedAmount);
        portfolio.setTotalReturnValue(totalReturnValue);
        portfolio.setTotalReturnRate(totalReturnRate);
        portfolio.setCreatedAt(LocalDateTime.now());

        investmentPortfolioRepository.save(portfolio);
        investmentPortfolioRepository.flush();

        return portfolio;
    }

    public InvestmentPortfolio getPortfoliosByUserInvestorId(UUID userInvestorId) {
        InvestmentPortfolio portfolio = investmentPortfolioRepository
                .findByUserInvestorId(userInvestorId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found for investorId: " + userInvestorId));
        return portfolio;
    }

    public boolean isExistPortfolio(InvestmentPortfolioRequest request) {
        return investmentPortfolioRepository.existsByUserInvestorId(request.getUserInvestorId());
    }

    // 전체 포트폴리오 조회
    public List<InvestmentPortfolio> getAllPortfolios() {
        return investmentPortfolioRepository.findAll();
    }

    @Transactional
    public InvestmentPortfolio updatePortfolioTotalInvestedAmount(InvestmentPortfolioRequest request) {
        UUID userInvestorId = request.getUserInvestorId();
        InvestmentPortfolio portfolio = investmentPortfolioRepository
                .findByUserInvestorId(userInvestorId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found for investorId: " + userInvestorId));
        List<Investment> investments = investmentRepository.findByUserInvestorId(userInvestorId);

        BigDecimal totalInvestedAmount = BigDecimal.ZERO;

        for (Investment investment : investments) {
            totalInvestedAmount = totalInvestedAmount.add(investment.getInvestmentAmount());
        }

        portfolio.setTotalInvestedAmount(totalInvestedAmount);
        investmentPortfolioRepository.save(portfolio);
        investmentPortfolioRepository.flush();

        return portfolio;
    }

    @Transactional
    public InvestmentPortfolio updateInvestmentPortfolioTotalReturnValueTotalReturnRate(InvestmentPortfolioRequest request) {
        UUID userInvestorId = request.getUserInvestorId();
        InvestmentPortfolio portfolio = investmentPortfolioRepository
                .findByUserInvestorId(userInvestorId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found for investorId: " + userInvestorId));
        List<Investment> investments = investmentRepository.findByUserInvestorId(userInvestorId);

        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalReturnValue = BigDecimal.ZERO;

        for (Investment investment : investments) {
            SettlementPrincipalAndProfitGetResponse response = settlementService.getTotalSettlementPrincipalAndProfit(investment.getInvestmentId());
            totalPrincipal = totalPrincipal.add(response.getTotalSettlementPrincipal());
            totalReturnValue = totalReturnValue.add(response.getTotalSettlementProfit());
        }

        BigDecimal totalReturnRate = totalReturnValue.divide(totalPrincipal, 2, RoundingMode.HALF_UP);

        portfolio.setTotalReturnValue(totalReturnValue);
        portfolio.setTotalReturnRate(totalReturnRate);

        investmentPortfolioRepository.save(portfolio);
        investmentPortfolioRepository.flush();

        return portfolio;
    }
}