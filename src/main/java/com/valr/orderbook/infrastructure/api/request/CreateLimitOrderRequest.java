package com.valr.orderbook.infrastructure.api.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.TimeInForce;

import java.math.BigDecimal;

public class CreateLimitOrderRequest {

    private BuySellSide side;

    private BigDecimal quantity;

    private int price;

    @JsonAlias("pair")
    private CurrencyPair currencyPair;

    private boolean postOnly;

    private String customerOrderId;

    private TimeInForce timeInForce;

    private boolean allowMargin;

    private boolean reduceOnly;

    public CreateLimitOrderRequest() { }

    public CreateLimitOrderRequest(BuySellSide side,
                                   BigDecimal quantity,
                                   int price,
                                   CurrencyPair currencyPair,
                                   boolean postOnly,
                                   String customerOrderId,
                                   TimeInForce timeInForce,
                                   boolean allowMargin,
                                   boolean reduceOnly) {
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.currencyPair = currencyPair;
        this.postOnly = postOnly;
        this.customerOrderId = customerOrderId;
        this.timeInForce = timeInForce;
        this.allowMargin = allowMargin;
        this.reduceOnly = reduceOnly;
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
}
