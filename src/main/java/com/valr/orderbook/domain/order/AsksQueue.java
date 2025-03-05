package com.valr.orderbook.domain.order;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AsksQueue extends PriorityQueue<LimitOrder> {
    public AsksQueue() {
        super(Comparator.comparing(LimitOrder::getPrice, Comparator.naturalOrder()));
    }
}
