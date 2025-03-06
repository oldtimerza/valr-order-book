package com.valr.orderbook.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBidsQueue {

    @Test
    @DisplayName("Should order bids by highest bid first")
    public void shouldOrderBidsHighestFirst() {
        BidsQueue bidsQueue = new BidsQueue();

        UUID highestId = UUID.randomUUID();
        LimitOrder highestLimitOrder = new LimitOrder.Builder()
                .id(highestId)
                .price(1000)
                .build();
        bidsQueue.add(highestLimitOrder);
        UUID midId = UUID.randomUUID();
        LimitOrder midLimitOrder = new LimitOrder.Builder()
                .id(midId)
                .price(900)
                .build();
        bidsQueue.add(midLimitOrder);
        UUID lowestId = UUID.randomUUID();
        LimitOrder lowLimitOrder = new LimitOrder.Builder()
                .id(lowestId)
                .price(800)
                .build();
        bidsQueue.add(lowLimitOrder);

        assertEquals(3, bidsQueue.size());
        assertEquals(highestId, bidsQueue.poll().getId());
        assertEquals(midId, bidsQueue.poll().getId());
        assertEquals(lowestId, bidsQueue.poll().getId());
    }
}
