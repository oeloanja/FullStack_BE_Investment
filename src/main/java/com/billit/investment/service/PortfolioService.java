package com.billit.investment.service;

import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.dto.PortfolioResponse;
import com.billit.investment.repository.InvestmentPortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final InvestmentPortfolioRepository portfolioRepository;

    public PortfolioService(InvestmentPortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    // 포트폴리오 생성
    public PortfolioResponse createPortfolio(Long userInvestorId, String portfolioName, BigDecimal totalInvestedAmount) {
        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        portfolio.setUserInvestorId(userInvestorId);
        portfolio.setPortfolioName(portfolioName);
        portfolio.setRiskLevel("MEDIUM");  // 기본 위험도
        portfolio.setTargetReturnRate(BigDecimal.valueOf(5.00));  // 기본 목표 수익률
        portfolio.setTotalInvestedAmount(totalInvestedAmount);
        portfolio.setActualReturnValue(BigDecimal.ZERO); // 초기 수익값
        portfolio.setActualReturnRate(BigDecimal.ZERO);  // 초기 수익률
        portfolio.setCreatedAt(LocalDateTime.now());

        InvestmentPortfolio savedPortfolio = portfolioRepository.save(portfolio);

        return new PortfolioResponse(
                savedPortfolio.getPortfolioId(),
                savedPortfolio.getPortfolioName(),
                savedPortfolio.getTotalInvestedAmount(),
                savedPortfolio.getRiskLevel()
        );
    }

    // 특정 투자자의 포트폴리오 조회
    public List<PortfolioResponse> getPortfoliosByUser(Long userInvestorId) {
        List<InvestmentPortfolio> portfolios = portfolioRepository.findByUserInvestorId(userInvestorId);
        return portfolios.stream()
                .map(portfolio -> new PortfolioResponse(
                        portfolio.getPortfolioId(),
                        portfolio.getPortfolioName(),
                        portfolio.getTotalInvestedAmount(),
                        portfolio.getRiskLevel()
                ))
                .collect(Collectors.toList());
    }
}

