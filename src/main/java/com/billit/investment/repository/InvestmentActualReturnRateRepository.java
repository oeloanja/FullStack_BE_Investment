package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentActualReturnRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvestmentActualReturnRateRepository extends JpaRepository<InvestmentActualReturnRate, Integer> {

    @Query("SELECT r FROM InvestmentActualReturnRate r WHERE r.investmentId = :investmentId ORDER BY r.createdAt DESC")
    List<InvestmentActualReturnRate> findByInvestmentId(@Param("investmentId") Integer investmentId);
}

