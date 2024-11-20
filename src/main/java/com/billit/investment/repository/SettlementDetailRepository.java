package com.billit.investment.repository;

import com.billit.investment.domain.SettlementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SettlementDetailRepository extends JpaRepository<SettlementDetail, Integer> {
    // 이 두 친구들을 활용하는 부분 전부 고쳐야함(investmentId -> settlementId 활용하도록)
    BigDecimal findTotalProfitBySettlementId(Integer settlementId);
    BigDecimal findTotalPrincipalBySettlementId(Integer settlementId);

    Integer countBySettlementId(Integer settlementId);

    @Query("SELECT SUM(sd.settlementPrincipal) FROM SettlementDetail sd " +
            "WHERE sd.settlementId IN (SELECT s.settlementId FROM Settlement s " +
            "WHERE s.investmentId IN (SELECT i.investmentId FROM Investment i WHERE i.userInvestorId = :investorId))")
    BigDecimal findTotalPrincipalByInvestorId(@Param("investorId") Integer investorId);

    @Query("SELECT SUM(sd.settlementProfit) FROM SettlementDetail sd " +
            "WHERE sd.settlementId IN (SELECT s.settlementId FROM Settlement s " +
            "WHERE s.investmentId IN (SELECT i.investmentId FROM Investment i WHERE i.userInvestorId = :investorId))")
    BigDecimal findTotalProfitByInvestorId(@Param("investorId") Integer investorId);
}
