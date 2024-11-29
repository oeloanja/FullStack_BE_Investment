package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Integer> {
    Optional<InvestmentPortfolio> findByUserInvestorId(Integer userInvestorId);
    boolean existsByUserInvestorId(Integer userInvestorId);
}