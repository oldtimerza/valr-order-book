package com.valr.orderbook.domain.trade;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.LimitOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Trade {
        final int price;
        final BigDecimal quantity;
        final CurrencyPair currencyPair;
        final Instant tradedAt;
        final BuySellSide takerSide;
        final UUID id;
        final BigDecimal quoteVolume;

    public Trade(int price,
                 BigDecimal quantity,
                 CurrencyPair currencyPair,
                 Instant tradedAt,
                 BuySellSide takerSide,
                 UUID id,
                 BigDecimal quoteVolume) {
        this.price = price;
        this.quantity = quantity;
        this.currencyPair = currencyPair;
        this.tradedAt = tradedAt;
        this.takerSide = takerSide;
        this.id = id;
        this.quoteVolume = quoteVolume;
    }

    public static Trade forOrder(LimitOrder limitOrder) {
        return new Trade(
                limitOrder.getPrice(),
                limitOrder.getQuantity(),
                limitOrder.getCurrencyPair(),
                Instant.now(),
                limitOrder.getSide(),
                UUID.randomUUID(),
                limitOrder.getVolume()
        );
    }

    public CurrencyPair getCurrencyPair() {
        return this.currencyPair;
    }

}
