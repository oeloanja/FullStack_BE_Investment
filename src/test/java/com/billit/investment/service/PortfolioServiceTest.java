package com.billit.investment.service;

import com.billit.investment.domain.InvestmentPortfolio;
import com.billit.investment.dto.PortfolioResponse;
import com.billit.investment.repository.InvestmentPortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @Mock
    private InvestmentPortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPortfolio_validData_shouldSavePortfolio() {
        // Given
        InvestmentPortfolio portfolio = new InvestmentPortfolio();
        portfolio.setPortfolioId(1L);
        portfolio.setPortfolioName("안정형 포트폴리오");
        portfolio.setTotalInvestedAmount(BigDecimal.valueOf(10000));
        portfolio.setRiskLevel("MEDIUM");

        when(portfolioRepository.save(any())).thenReturn(portfolio);

        // When
        PortfolioResponse response = portfolioService.createPortfolio(1L, "안정형 포트폴리오", BigDecimal.valueOf(10000));

        // Then
        assertNotNull(response);
        assertEquals("안정형 포트폴리오", response.getPortfolioName());
        verify(portfolioRepository, times(1)).save(any());
    }

    @Test
    void getPortfoliosByUser_validUserId_shouldReturnPortfolios() {
        // Given
        InvestmentPortfolio portfolio1 = new InvestmentPortfolio();
        portfolio1.setPortfolioId(1L);
        portfolio1.setPortfolioName("안정형 포트폴리오");
        portfolio1.setTotalInvestedAmount(BigDecimal.valueOf(10000));
        portfolio1.setRiskLevel("LOW");

        InvestmentPortfolio portfolio2 = new InvestmentPortfolio();
        portfolio2.setPortfolioId(2L);
        portfolio2.setPortfolioName("고위험 포트폴리오");
        portfolio2.setTotalInvestedAmount(BigDecimal.valueOf(50000));
        portfolio2.setRiskLevel("HIGH");

        when(portfolioRepository.findByUserInvestorId(1L)).thenReturn(Arrays.asList(portfolio1, portfolio2));

        // When
        List<PortfolioResponse> responses = portfolioService.getPortfoliosByUser(1L);

        // Then
        assertEquals(2, responses.size());
        assertEquals("안정형 포트폴리오", responses.get(0).getPortfolioName());
        assertEquals("고위험 포트폴리오", responses.get(1).getPortfolioName());
        verify(portfolioRepository, times(1)).findByUserInvestorId(1L);
    }
}

