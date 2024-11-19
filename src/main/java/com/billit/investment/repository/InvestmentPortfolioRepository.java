package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Integer> {
    List<InvestmentPortfolio> findByUserInvestorId(Integer userInvestorId);
}


