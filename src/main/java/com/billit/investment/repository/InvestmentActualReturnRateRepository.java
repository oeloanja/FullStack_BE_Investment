package com.billit.investment.repository;

import com.billit.investment.domain.InvestmentActualReturnRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestmentActualReturnRateRepository extends JpaRepository<InvestmentActualReturnRate, Integer> {
    // 특정 투자 ID로 수익률 조회
    Optional<InvestmentActualReturnRate> findByInvestmentId(Integer investmentId);

    // 특정 투자 ID로 존재 여부 확인
    boolean existsByInvestmentId(Integer investmentId);
}

