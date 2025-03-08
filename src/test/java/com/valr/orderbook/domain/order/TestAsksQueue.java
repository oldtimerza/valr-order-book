package com.valr.orderbook.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAsksQueue {

    @Test
    @DisplayName("Should order asks by lowest ask first")
    public void shouldOrderAsksLowestFirst() {
        AsksQueue asksQueue = new AsksQueue();

        UUID highestId = UUID.randomUUID();
        LimitOrder highestLimitOrder = new LimitOrder.Builder()
                .id(highestId)
                .price(1000)
                .build();
        asksQueue.add(highestLimitOrder);
        UUID midId = UUID.randomUUID();
        LimitOrder midLimitOrder = new LimitOrder.Builder()
                .id(midId)
                .price(900)
                .build();
        asksQueue.add(midLimitOrder);
        UUID lowestId = UUID.randomUUID();
        LimitOrder lowLimitOrder = new LimitOrder.Builder()
                .id(lowestId)
                .price(800)
                .build();
        asksQueue.add(lowLimitOrder);

        assertEquals(3, asksQueue.size());
        assertEquals(lowestId, asksQueue.poll().getId());
        assertEquals(midId, asksQueue.poll().getId());
        assertEquals(highestId, asksQueue.poll().getId());
    }
}
