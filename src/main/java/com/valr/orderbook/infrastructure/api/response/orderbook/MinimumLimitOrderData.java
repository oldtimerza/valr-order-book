package com.valr.orderbook.infrastructure.api.response.orderbook;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.LimitOrder;

import java.math.BigDecimal;

public class MinimumLimitOrderData {
    private final BuySellSide side;
    private final BigDecimal quantity;
    private final int price;
    private final CurrencyPair currencyPair;
    private final int orderCount;

    private MinimumLimitOrderData(BuySellSide side, BigDecimal quantity, int price, CurrencyPair currencyPair, int orderCount) {
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.currencyPair = currencyPair;
        this.orderCount = orderCount;
    }

    public static MinimumLimitOrderData from(LimitOrder limitOrder) {
        return new MinimumLimitOrderData(
                limitOrder.getSide(),
                limitOrder.getQuantity(),
                limitOrder.getPrice(),
                limitOrder.getCurrencyPair(),
                limitOrder.getOrderCount()
        );
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BuySellSide getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public int getOrderCount() {
        return orderCount;
    }
}
