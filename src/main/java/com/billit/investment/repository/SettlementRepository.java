package com.billit.investment.repository;

import com.billit.investment.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
    List<Settlement> findByInvestmentId(Integer investmentId);
}

