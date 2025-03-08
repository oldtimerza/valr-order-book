package com.valr.orderbook.domain.order;

import java.util.Comparator;

public class AsksComparator implements Comparator<LimitOrder> {
    @Override
    public int compare(LimitOrder firstLimitOrder, LimitOrder secondLimitOrder) {
        int priceCompare = Integer.compare(firstLimitOrder.getPrice(), secondLimitOrder.getPrice());
        return priceCompare != 0 ? priceCompare : firstLimitOrder.getCreatedAt().compareTo(secondLimitOrder.getCreatedAt());
    }
}
