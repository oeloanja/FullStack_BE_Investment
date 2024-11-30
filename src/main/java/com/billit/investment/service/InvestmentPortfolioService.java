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

@Service
@RequiredArgsConstructor
public class InvestmentPortfolioService {
    private final InvestmentRepository investmentRepository;
    private final InvestmentPortfolioRepository investmentPortfolioRepository;
    private final SettlementService settlementService;

    @Transactional
    public InvestmentPortfolio createInvestmentPortfolio(InvestmentPortfolioRequest request) {
        Integer userInvestorId = request.getUserInvestorId();
        List<Investment> investments = investmentRepository.findByUserInvestorId(userInvestorId);

        BigDecimal totalInvestedAmount = BigDecimal.ZERO;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalReturnValue = BigDecimal.ZERO;

        for (Investment investment : investments) {
            SettlementPrincipalAndProfitGetResponse response = settlementService.getTotalSettlementPrincipalAndProfit(investment.getInvestmentId());
            totalInvestedAmount = totalInvestedAmount.add(investment.getInvestmentAmount());
            totalPrincipal = totalPrincipal.add(response.getTotalSettlementPrincipal());
            totalReturnValue = totalReturnValue.add(response.getTotalSettlementProfit());
        }

        BigDecimal totalReturnRate = totalReturnValue.divide(totalPrincipal, 2, RoundingMode.HALF_UP);

        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        portfolio.setUserInvestorId(userInvestorId);
        portfolio.setTotalInvestedAmount(totalInvestedAmount);
        portfolio.setTotalReturnValue(totalReturnValue);
        portfolio.setTotalReturnRate(totalReturnRate);
        portfolio.setCreatedAt(LocalDateTime.now());

        return investmentPortfolioRepository.save(portfolio);
    }

    public InvestmentPortfolio getPortfoliosByUserInvestorId(Integer userInvestorId) {
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
    public InvestmentPortfolio updateInvestmentPortfolio(InvestmentPortfolioRequest request) {
        Integer userInvestorId = request.getUserInvestorId();
        InvestmentPortfolio portfolio = investmentPortfolioRepository
                .findByUserInvestorId(userInvestorId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found for investorId: " + userInvestorId));
        List<Investment> investments = investmentRepository.findByUserInvestorId(userInvestorId);

        BigDecimal totalInvestedAmount = BigDecimal.ZERO;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalReturnValue = BigDecimal.ZERO;

        for (Investment investment : investments) {
            SettlementPrincipalAndProfitGetResponse response = settlementService.getTotalSettlementPrincipalAndProfit(investment.getInvestmentId());
            totalInvestedAmount = totalInvestedAmount.add(investment.getInvestmentAmount());
            totalPrincipal = totalPrincipal.add(response.getTotalSettlementPrincipal());
            totalReturnValue = totalReturnValue.add(response.getTotalSettlementProfit());
        }

        BigDecimal totalReturnRate = totalReturnValue.divide(totalPrincipal, 2, RoundingMode.HALF_UP);

        portfolio.setTotalInvestedAmount(totalInvestedAmount);
        portfolio.setTotalReturnValue(totalReturnValue);
        portfolio.setTotalReturnRate(totalReturnRate);

        return investmentPortfolioRepository.save(portfolio);
    }
}