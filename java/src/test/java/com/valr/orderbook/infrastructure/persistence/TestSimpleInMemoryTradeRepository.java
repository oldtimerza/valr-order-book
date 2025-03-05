package com.valr.orderbook.infrastructure.persistence;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.trade.Trade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSimpleInMemoryTradeRepository {
    private List<Trade> trades;
    private SimpleInMemoryTradeRepository simpleInMemoryTradeRepository;

    @BeforeEach
    public void setup() {
        trades = new ArrayList<>(){{
            add(new Trade(
                    100,
                    BigDecimal.valueOf(0.05),
                    CurrencyPair.BTCZAR,
                    LocalDateTime.now(),
                    BuySellSide.SELL,
                    UUID.randomUUID(),
                    BigDecimal.valueOf(5)
            ));
            add(new Trade(
                    100,
                    BigDecimal.valueOf(0.05),
                    CurrencyPair.BTCZAR,
                    LocalDateTime.now(),
                    BuySellSide.BUY,
                    UUID.randomUUID(),
                    BigDecimal.valueOf(5)
            ));
        }};
        simpleInMemoryTradeRepository = new SimpleInMemoryTradeRepository(trades);
    }

    @Test
    @DisplayName("Should save new trades")
    public void shouldSaveNewTrades() {
        Trade newTrade = new Trade(
                200,
                BigDecimal.valueOf(0.05),
                CurrencyPair.BTCZAR,
                LocalDateTime.now(),
                BuySellSide.BUY,
                UUID.randomUUID(),
                BigDecimal.valueOf(10)
        );

        List<Trade> tradesToSave = new ArrayList<>(){{
            add(newTrade);
        }};
        simpleInMemoryTradeRepository.saveTrades(tradesToSave, error -> {
            Assertions.fail();
        });

        assertTrue(trades.contains(newTrade));
    }

    @Test
    @DisplayName("Should retrieve trades history")
    public void shouldRetrieveTradeHistory() {
        simpleInMemoryTradeRepository.fetchTrades(
                CurrencyPair.BTCZAR,
                actualTrades -> {
                    assertEquals(trades, actualTrades);
                },
                error -> {
                    Assertions.fail();
                });
    }
}
