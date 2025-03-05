package com.valr.orderbook.domain;

public class InvalidCurrencyPairException extends RuntimeException {
    public InvalidCurrencyPairException(final String currencyPair) {
        super("Invalid currency pair provided: " + currencyPair);
    }
}
