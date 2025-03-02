package com.valr.orderbook.domain;

public enum BuySellSide {
    BUY("buy"),
    SELL("sell");

    public final String label;

    private BuySellSide(final String label) {
        this.label = label;
    }
}
