package com.billit.investment.service;

import com.billit.investment.domain.Investment;
import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.repository.InvestmentPortfolioRepository;
import com.billit.investment.repository.InvestmentRepository;
import com.billit.investment.repository.SettlementDetailRepository;
import com.billit.investment.repository.SettlementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentPortfolioService {
    private final InvestmentRepository investmentRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final InvestmentPortfolioRepository investmentPortfolioRepository;

    @Transactional
    public InvestmentPortfolio createPortfolio(Integer userInvestorId) {
        List<Investment> investments = investmentRepository.findByUserInvestorId(userInvestorId);

        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Investment investment : investments) {
            totalPrincipal = totalPrincipal.add(settlementDetailRepository.findTotalPrincipalBySettlementId(investment.getInvestmentId()));
            totalProfit = totalProfit.add(settlementDetailRepository.findTotalProfitBySettlementId(investment.getInvestmentId()));
        }

        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        portfolio.setUserInvestorId(userInvestorId);
        portfolio.setTotalInvestedAmount(totalPrincipal);
        portfolio.setActualReturnValue(totalProfit);
        portfolio.setCreatedAt(LocalDateTime.now());

        return portfolio;
    }

    @Transactional
    public void updatePortfolio(Integer userInvestorId) {
        InvestmentPortfolio portfolio = investmentPortfolioRepository.findByUserInvestorIdReturnOptional(userInvestorId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found for investorId: " + userInvestorId));

        BigDecimal totalPrincipal = settlementDetailRepository.findTotalPrincipalByInvestorId(userInvestorId);
        BigDecimal totalProfit = settlementDetailRepository.findTotalProfitByInvestorId(userInvestorId);

        portfolio.setUserInvestorId(userInvestorId);
        portfolio.setTotalInvestedAmount(totalPrincipal);
        portfolio.setActualReturnValue(totalProfit);

        investmentPortfolioRepository.save(portfolio);
    }

    // 사용자별 포트폴리오 조회
    public List<InvestmentPortfolio> getPortfoliosByUser(Integer userInvestorId) {
        return investmentPortfolioRepository.findByUserInvestorId(userInvestorId);
    }

    // 전체 포트폴리오 조회
    public List<InvestmentPortfolio> getAllPortfolios() {
        return investmentPortfolioRepository.findAll();
    }
}


