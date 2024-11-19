package com.billit.investment.repository;

import com.billit.investment.domain.InvestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestStatusRepository extends JpaRepository<InvestStatus, Integer> {
}

