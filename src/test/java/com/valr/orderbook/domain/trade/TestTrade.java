package com.valr.orderbook.domain.trade;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTrade {

    @Test
    @DisplayName("Should have it's values set correcly")
    public void shouldHaveValuesSet() {
        int price = 1000;
        BigDecimal quantity = BigDecimal.ONE;
        CurrencyPair currencyPair = CurrencyPair.BTCZAR;
        LocalDateTime tradedAt = LocalDateTime.now();
        BuySellSide takerSide = BuySellSide.BUY;
        UUID id = UUID.randomUUID();
        BigDecimal quoteVolume = BigDecimal.valueOf(1000);

        Trade trade = new Trade(
                price,
                quantity,
                currencyPair,
                tradedAt,
                takerSide,
                id,
                quoteVolume
        );

        assertEquals(price, trade.getPrice());
        assertEquals(quantity, trade.getQuantity());
        assertEquals(currencyPair, trade.getCurrencyPair());
        assertEquals(tradedAt, trade.getTradedAt());
        assertEquals(takerSide, trade.getTakerSide());
        assertEquals(id, trade.getId());
        assertEquals(quoteVolume, trade.getQuoteVolume());
    }
}
