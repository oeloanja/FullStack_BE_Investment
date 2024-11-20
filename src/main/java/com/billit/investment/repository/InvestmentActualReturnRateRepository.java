package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentActualReturnRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestmentActualReturnRateRepository extends JpaRepository<InvestmentActualReturnRate, Integer> {
    Optional<InvestmentActualReturnRate> findByInvestmentId(Integer investmentId);
}

