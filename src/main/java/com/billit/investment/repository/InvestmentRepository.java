package com.billit.investment.repository;

import com.billit.investment.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserInvestorId(Long userInvestorId);
}
