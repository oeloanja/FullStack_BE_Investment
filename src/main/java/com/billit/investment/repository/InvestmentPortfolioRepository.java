package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Long> {

    /**
     * 특정 투자자의 포트폴리오 목록을 조회합니다.
     *
     * @param userInvestorId 투자자의 고유 ID
     * @return 투자자의 포트폴리오 목록
     */
    List<InvestmentPortfolio> findByUserInvestorId(Long userInvestorId);

    /**
     * 위험도에 따라 포트폴리오를 조회합니다.
     *
     * @param riskLevel 위험도 (예: LOW, MEDIUM, HIGH)
     * @return 해당 위험도에 속하는 포트폴리오 목록
     */
    List<InvestmentPortfolio> findByRiskLevel(String riskLevel);

    /**
     * 특정 사용자와 이름이 일치하는 포트폴리오를 조회합니다.
     *
     * @param userInvestorId 투자자의 고유 ID
     * @param portfolioName 포트폴리오 이름
     * @return 포트폴리오 엔티티
     */
    InvestmentPortfolio findByUserInvestorIdAndPortfolioName(Long userInvestorId, String portfolioName);
}

