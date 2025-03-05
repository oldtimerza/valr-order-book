package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.domain.trade.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

public class OrderBook {
    private final CurrencyPair currencyPair;

    private final PriorityQueue<LimitOrder> asks;

    private final PriorityQueue<LimitOrder> bids;

    private LocalDateTime lastChange;

    private int sequenceNumber;

    public OrderBook(CurrencyPair currencyPair, PriorityQueue<LimitOrder> asks, PriorityQueue<LimitOrder> bids) {
        this.currencyPair = currencyPair;
        this.asks = asks;
        this.bids = bids;
    }

    public PriorityQueue<LimitOrder> getAsks() {
        return asks;
    }

    public PriorityQueue<LimitOrder> getBids() {
        return bids;
    }

    public LocalDateTime getLastChange() {
        return lastChange;
    }

    public void setLastChange(LocalDateTime lastChange) {
        this.lastChange = lastChange;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void placeLimitOrder(LimitOrder limitOrder) {
        if(limitOrder.getCurrencyPair() != this.currencyPair){
            throw new InvalidCurrencyPairException(limitOrder.getCurrencyPair().toString());
        }
        if(limitOrder.getSide() == BuySellSide.SELL) {
            asks.add(limitOrder);
        } else {
            bids.add(limitOrder);
        }
    }

    public List<Trade> tradeOnMatchingLimitOrders() {
        boolean matchingOrders = true;

        List<Trade> tradesOccured = new ArrayList<>();

        while(matchingOrders) {
            LimitOrder highestBid = bids.peek();
            LimitOrder lowestAsk = asks.peek();

            if(highestBid == null || lowestAsk == null) {
                break;
            }

            if(highestBid.getPrice() >= lowestAsk.getPrice()) {
                final int sellPrice = lowestAsk.getPrice();
                final BigDecimal sellQuantity = lowestAsk.getQuantity();
                final BigDecimal buyQuantity = highestBid.getQuantity();

                LocalDateTime timeOfTrade = LocalDateTime.now();

                BigDecimal tradeQuantity;

                if(buyQuantity.compareTo(sellQuantity) > 0) {
                    tradeQuantity = sellQuantity;
                    highestBid.subtractQuantity(sellQuantity);
                    asks.poll();
                } else if(buyQuantity.compareTo(sellQuantity) < 0) {
                    tradeQuantity = buyQuantity;
                    lowestAsk.subtractQuantity(buyQuantity);
                    bids.poll();
                } else {
                    tradeQuantity = sellQuantity;
                    asks.poll();
                    bids.poll();
                }

                Trade sellTrade = new Trade(
                        sellPrice,
                        tradeQuantity,
                        this.currencyPair,
                        timeOfTrade,
                        BuySellSide.SELL,
                        UUID.randomUUID(),
                        BigDecimal.valueOf(sellPrice).multiply(tradeQuantity)
                );
                tradesOccured.add(sellTrade);

                Trade buyTrade = new Trade(
                        sellPrice,
                        tradeQuantity,
                        this.currencyPair,
                        timeOfTrade,
                        BuySellSide.BUY,
                        UUID.randomUUID(),
                        BigDecimal.valueOf(sellPrice).multiply(tradeQuantity)
                );
                tradesOccured.add(buyTrade);
            } else {
                matchingOrders = false;
            }
        }

        return tradesOccured;
    }

}
