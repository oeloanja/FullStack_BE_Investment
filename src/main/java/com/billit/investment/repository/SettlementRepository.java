package com.billit.investment.repository;

import com.billit.investment.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {

    // 특정 investmentId로 Settlement 조회
    Optional<Settlement> findByInvestmentId(Integer investmentId);

    // 특정 groupId에 속한 모든 Settlement의 정산 비율 합계 계산 (예시: 통계 목적)
    @Query("SELECT SUM(s.settlementRatio) FROM Settlement s WHERE s.investmentId IN " +
            "(SELECT i.investmentId FROM Investment i WHERE i.groupId = :groupId)")
    Double findTotalSettlementRatioByGroupId(@Param("groupId") Integer groupId);

    // 특정 investmentId의 정산 비율을 가져오기
    @Query("SELECT s.settlementRatio FROM Settlement s WHERE s.investmentId = :investmentId")
    Double findSettlementRatioByInvestmentId(@Param("investmentId") Integer investmentId);
}

