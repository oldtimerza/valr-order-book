package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.TimeInForce;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LimitOrder {
    private final UUID id;
    private final BuySellSide side;
    private final int price;
    private final CurrencyPair currencyPair;
    private final int orderCount;
    private final boolean postOnly;
    private final String customerOrderId;
    private final TimeInForce timeInForce;
    private final boolean allowMargin;
    private final boolean reduceOnly;
    private final LocalDateTime createdAt;
    private BigDecimal quantity;

    private LimitOrder(Builder builder) {
        this.id = builder.id;
        this.side = builder.side;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.currencyPair = builder.currencyPair;
        this.orderCount = builder.orderCount;
        this.postOnly = builder.postOnly;
        this.customerOrderId = builder.customerOrderId;
        this.timeInForce = builder.timeInForce;
        this.allowMargin = builder.allowMargin;
        this.reduceOnly = builder.reduceOnly;
        this.createdAt = builder.createdAt;
    }

    public UUID getId() { return id; }
    public BuySellSide getSide() { return side; }
    public BigDecimal getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public CurrencyPair getCurrencyPair() { return currencyPair; }
    public int getOrderCount() { return orderCount; }
    public boolean isPostOnly() { return postOnly; }
    public String getCustomerOrderId() { return customerOrderId; }
    public TimeInForce getTimeInForce() { return timeInForce; }
    public boolean isAllowMargin() { return allowMargin; }
    public boolean isReduceOnly() { return reduceOnly; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void subtractQuantity(final BigDecimal amount) {
        setQuantity(this.quantity.subtract(amount));
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public static class Builder {
        private UUID id = UUID.randomUUID(); // Default to a new UUID
        private BuySellSide side;
        private BigDecimal quantity;
        private int price;
        private CurrencyPair currencyPair;
        private int orderCount = 1; // Default order count to 1
        private boolean postOnly = false;
        private String customerOrderId = "";
        private TimeInForce timeInForce;
        private boolean allowMargin = false;
        private boolean reduceOnly = false;
        private LocalDateTime createdAt = LocalDateTime.now(); // Default to current time

        public Builder side(BuySellSide side) {
            this.side = side;
            return this;
        }

        public Builder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder currencyPair(CurrencyPair currencyPair) {
            this.currencyPair = currencyPair;
            return this;
        }

        public Builder orderCount(int orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public Builder postOnly(boolean postOnly) {
            this.postOnly = postOnly;
            return this;
        }

        public Builder customerOrderId(String customerOrderId) {
            this.customerOrderId = customerOrderId;
            return this;
        }

        public Builder timeInForce(TimeInForce timeInForce) {
            this.timeInForce = timeInForce;
            return this;
        }

        public Builder allowMargin(boolean allowMargin) {
            this.allowMargin = allowMargin;
            return this;
        }

        public Builder reduceOnly(boolean reduceOnly) {
            this.reduceOnly = reduceOnly;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public LimitOrder build() {
            return new LimitOrder(this);
        }
    }
}