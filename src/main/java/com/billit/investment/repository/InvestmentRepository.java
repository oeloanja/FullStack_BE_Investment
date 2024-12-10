package com.billit.investment.repository;

import com.billit.investment.domain.Investment;
import com.billit.investment.dto.InvestmentWithStatusDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestmentRepository extends JpaRepository<Investment, Integer> {

    @Query("SELECT i, s FROM Investment i JOIN InvestStatus s ON i.investmentId = s.investmentId")
    List<Object[]> findInvestmentWithStatus();

    @Query("SELECT i, s FROM Investment i JOIN InvestStatus s ON i.investmentId = s.investmentId WHERE i.userInvestorId = :userInvestorId")
    List<Object[]> findInvestmentWithStatusByUserInvestorId(@Param("userInvestorId") UUID userInvestorId);

    @Query("SELECT new com.billit.investment.dto.InvestmentWithStatusDTO(i, s) FROM Investment i JOIN InvestStatus s ON i.investmentId = s.investmentId WHERE i.investmentId = :investmentId")
    Optional<InvestmentWithStatusDTO> findInvestmentWithStatusByInvestmentId(@Param("investmentId") Integer investmentId);

    List<Investment> findByUserInvestorId(UUID userInvestorId);
    List<Investment> findByGroupId(Integer groupId);

    BigDecimal findInvestmentAmountByInvestmentId(Integer investmentId);

    Optional<Investment> findTopByGroupIdOrderByCreatedAtDesc(Integer groupId);

    @Modifying
    @Query("UPDATE Investment i SET i.investmentAmount = :amount WHERE i.investmentId = :investmentId")
    void updateInvestmentAmount(@Param("investmentId") Integer investmentId, @Param("amount") BigDecimal amount);
}
