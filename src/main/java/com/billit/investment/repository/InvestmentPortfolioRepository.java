package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Integer> {
    Optional<InvestmentPortfolio> findByUserInvestorId(UUID userInvestorId);
    boolean existsByUserInvestorId(UUID userInvestorId);
}