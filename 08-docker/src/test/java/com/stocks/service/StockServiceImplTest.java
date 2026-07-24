package com.stocks.service;

import com.stocks.model.Stock;
import com.stocks.repository.HistoricalPriceRepository;
import com.stocks.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTest {

    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private HistoricalPriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        stockService = new StockServiceImpl(stockRepository, priceRepository);
    }

    @Test
    void testAddStockSuccess() {
        // Given
        Stock newStock = new Stock(null, "AAPL", "Apple Inc.", "Technology", "NASDAQ");
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.empty());
        when(stockRepository.save(newStock)).thenReturn(new Stock(1L, "AAPL", "Apple Inc.", "Technology", "NASDAQ"));

        // When
        Stock result = stockService.addStock(newStock);

        // Then
        assertNotNull(result);
        assertEquals("AAPL", result.symbol());
        verify(stockRepository).save(newStock);
    }

    @Test
    void testAddStockWithDuplicateSymbol() {
        // Given
        Stock existingStock = new Stock(1L, "AAPL", "Apple Inc.", "Technology", "NASDAQ");
        Stock newStock = new Stock(null, "AAPL", "Apple Inc.", "Technology", "NASDAQ");
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(existingStock));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stockService.addStock(newStock);
        });
        assertTrue(exception.getMessage().contains("Duplicate symbol"));
    }
}

