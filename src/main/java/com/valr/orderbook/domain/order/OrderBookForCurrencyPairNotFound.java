package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.CurrencyPair;

public class OrderBookForCurrencyPairNotFound extends RuntimeException {
    public OrderBookForCurrencyPairNotFound(final CurrencyPair currencyPair) {
        super("An orderbook for the given currencypair: " +currencyPair.name()+ "does not exist.");
    }
}
