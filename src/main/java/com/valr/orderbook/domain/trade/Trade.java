package com.valr.orderbook.domain.trade;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Trade {
    private final int price;
    private final BigDecimal quantity;
    private final CurrencyPair currencyPair;
    private final LocalDateTime tradedAt;
    private final BuySellSide takerSide;
    private final UUID id;
    private final BigDecimal quoteVolume;

    public Trade(int price,
                 BigDecimal quantity,
                 CurrencyPair currencyPair,
                 LocalDateTime tradedAt,
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

    public CurrencyPair getCurrencyPair() {
        return this.currencyPair;
    }

    public int getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getTradedAt() {
        return tradedAt;
    }

    public BuySellSide getTakerSide() {
        return takerSide;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }
}
