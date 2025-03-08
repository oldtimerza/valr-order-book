package com.valr.orderbook.domain.order;

import java.util.PriorityQueue;

public class BidsQueue extends PriorityQueue<LimitOrder> {
    public BidsQueue() {
        super(new BidsComparator());
    }
}
