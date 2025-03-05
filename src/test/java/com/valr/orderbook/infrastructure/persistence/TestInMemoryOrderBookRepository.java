package com.valr.orderbook.infrastructure.persistence;

import com.valr.orderbook.domain.CurrencyPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestInMemoryOrderBookRepository {
    private SimpleInMemoryOrderBookRepository simpleInMemoryOrderBookRepository;

    @BeforeEach
    public void setup() {
        simpleInMemoryOrderBookRepository = new SimpleInMemoryOrderBookRepository();
    }

    @Test
    @DisplayName("Should fetch orderbook by currency pair")
    public void shouldReturnOrderBook() {
        simpleInMemoryOrderBookRepository.fetchOrderBookForCurrencyPairAsync(CurrencyPair.BTCZAR,
                orderBook -> {

                },
                error -> {
                    Assertions.fail();
                }
        );
    }
}
