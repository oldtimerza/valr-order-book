package com.valr.orderbook.infrastructure.api.response;

import com.valr.orderbook.domain.order.LimitOrder;

import java.util.UUID;

public record LimitOrderIdOnly(UUID id) {

    public static LimitOrderIdOnly from(LimitOrder limitOrder) {
        return new LimitOrderIdOnly(limitOrder.getId());
    }
}
