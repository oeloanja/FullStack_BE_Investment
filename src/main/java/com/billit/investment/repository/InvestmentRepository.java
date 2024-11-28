package com.billit.investment.repository;

import com.billit.investment.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Integer> {

    @Query("SELECT i, s FROM Investment i JOIN InvestStatus s ON i.investmentId = s.investmentId")
    List<Object[]> findInvestmentWithStatus();

    @Query("SELECT i, s FROM Investment i JOIN InvestStatus s ON i.investmentId = s.investmentId WHERE i.userInvestorId = :userInvestorId")
    List<Object[]> findInvestmentWithStatusByUserInvestorId(@Param("userInvestorId") Integer userInvestorId);

    List<Investment> findByUserInvestorId(Integer userInvestorId);
    List<Investment> findByGroupId(Integer groupId);

    BigDecimal findInvestmentAmountByInvestmentId(Integer investmentId);
}
