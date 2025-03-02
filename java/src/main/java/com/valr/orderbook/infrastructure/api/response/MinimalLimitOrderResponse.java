package com.valr.orderbook.infrastructure.api.response;

import com.valr.orderbook.domain.order.LimitOrder;

import java.util.UUID;

public class MinimalLimitOrderResponse {
    private final UUID id;

    public MinimalLimitOrderResponse(UUID id) {
        this.id = id;
    }

    public static MinimalLimitOrderResponse from(LimitOrder limitOrder) {
        return new MinimalLimitOrderResponse(limitOrder.getCustomerOrderId());
    }

    public UUID getId() {
        return id;
    }
}
