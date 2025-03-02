package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.TimeInForce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class LimitOrder {
    private final UUID id;
    private final BuySellSide side;
    private final BigDecimal quantity;
    private final int price;
    private final CurrencyPair currencyPair;
    private final int orderCount;
    private final boolean postOnly;
    private final String customerOrderId;
    private final TimeInForce timeInForce;
    private final boolean allowMargin;
    private final boolean reduceOnly;
    private final Instant createdAt;

    public LimitOrder(UUID id,
                      BuySellSide side,
                      BigDecimal quantity,
                      int price,
                      CurrencyPair currencyPair,
                      int orderCount,
                      boolean postOnly,
                      String customerOrderId,
                      TimeInForce timeInForce,
                      boolean allowMargin,
                      boolean reduceOnly,
                      Instant createdAt
   ) {
        this.id = id;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.currencyPair = currencyPair;
        this.orderCount = orderCount;
        this.postOnly = postOnly;
        this.customerOrderId = customerOrderId;
        this.timeInForce = timeInForce;
        this.allowMargin = allowMargin;
        this.reduceOnly = reduceOnly;
        this.createdAt = createdAt;
    }

    public BuySellSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
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

    public boolean isPostOnly() {
        return postOnly;
    }

    public String getCustomerOrderId() {
        return customerOrderId;
    }

    public TimeInForce getTimeInForce() {
        return timeInForce;
    }

    public boolean isAllowMargin() {
        return allowMargin;
    }

    public boolean isReduceOnly() {
        return reduceOnly;
    }

    public BigDecimal getVolume() {
        return BigDecimal.valueOf(this.price).multiply(this.quantity);
    }
}
