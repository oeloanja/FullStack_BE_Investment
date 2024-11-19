package com.billit.investment.repository;

import com.billit.investment.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Integer> {
    List<Investment> findByUserInvestorId(Integer userInvestorId);
    List<Investment> findByGroupId(Integer groupId);
}
