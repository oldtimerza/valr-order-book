package com.valr.orderbook.infrastructure.api.response;

public enum ErrorCode {
    E001("Orderbook not found."),
    E002("The given currencypair is invalid");

    private String description;

    ErrorCode(final String description) {
        this.description = description;
    }
}
