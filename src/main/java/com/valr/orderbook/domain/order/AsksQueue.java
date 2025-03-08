package com.valr.orderbook.domain.order;

import java.util.PriorityQueue;

public class AsksQueue extends PriorityQueue<LimitOrder> {
    public AsksQueue() {
        super(new AsksComparator());
    }
}
