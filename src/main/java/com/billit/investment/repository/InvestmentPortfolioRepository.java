package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Integer> {
    List<InvestmentPortfolio> findByUserInvestorId(Integer userInvestorId);

    @Query("SELECT p FROM InvestmentPortfolio p WHERE p.userInvestorId = :userInvestorId")
    Optional<InvestmentPortfolio> findByUserInvestorIdReturnOptional(@Param("userInvestorId") Integer userInvestorId);
}


