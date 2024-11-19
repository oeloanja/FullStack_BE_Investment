package com.billit.investment.service;

import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.dto.InvestmentPortfolioRequest;
import com.billit.investment.repository.InvestmentPortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentPortfolioService {
    private final InvestmentPortfolioRepository portfolioRepository;

    // 포트폴리오 생성
    public InvestmentPortfolio createPortfolio(InvestmentPortfolioRequest request) {
        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        portfolio.setUserInvestorId(request.getUserInvestorId());
        portfolio.setPortfolioName(request.getPortfolioName());
        portfolio.setRiskLevel(request.getRiskLevel());
        portfolio.setTargetReturnRate(request.getTargetReturnRate());
        portfolio.setTotalInvestedAmount(request.getTotalInvestedAmount());
        portfolio.setActualReturnValue(request.getActualReturnValue());
        portfolio.setActualReturnRate(request.getActualReturnRate());
        return portfolioRepository.save(portfolio);
    }

    // 포트폴리오 갱신
    public InvestmentPortfolio updatePortfolio(Integer portfolioId, InvestmentPortfolioRequest request) {
        InvestmentPortfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        portfolio.setTargetReturnRate(request.getTargetReturnRate());
        portfolio.setTotalInvestedAmount(request.getTotalInvestedAmount());
        portfolio.setActualReturnValue(request.getActualReturnValue());
        portfolio.setActualReturnRate(request.getActualReturnRate());
        return portfolioRepository.save(portfolio);
    }

    // 사용자별 포트폴리오 조회
    public List<InvestmentPortfolio> getPortfoliosByUser(Integer userInvestorId) {
        return portfolioRepository.findByUserInvestorId(userInvestorId);
    }

    // 전체 포트폴리오 조회
    public List<InvestmentPortfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }
}


