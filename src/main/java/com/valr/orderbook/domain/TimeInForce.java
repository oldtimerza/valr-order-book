package com.valr.orderbook.domain;

public enum TimeInForce {
    GoodTillCancelled("GTC"),
    FillOrKill("FOK"),
    ImmediateOrCancel("IOC");

    public final String label;

    private TimeInForce(final String label) {
        this.label = label;
    }
}
