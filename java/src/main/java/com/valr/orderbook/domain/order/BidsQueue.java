package com.valr.orderbook.domain.order;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BidsQueue extends PriorityQueue<LimitOrder> {
    public BidsQueue() {
        super(Comparator.comparing(limitOrder -> -limitOrder.getPrice(), Comparator.naturalOrder()));
    }
}
