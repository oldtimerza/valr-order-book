package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.domain.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class OrderBook {
    private final CurrencyPair currencyPair;

    private final PriorityQueue<LimitOrder> asks;

    private final PriorityQueue<LimitOrder> bids;

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

    public LimitOrder placeLimitOrder(LimitOrder limitOrder) {
        if(limitOrder.getCurrencyPair() != this.currencyPair){
            throw new InvalidCurrencyPairException(limitOrder.getCurrencyPair().toString());
        }
        if(limitOrder.getSide() == BuySellSide.SELL) {
            asks.add(limitOrder);
        } else {
            bids.add(limitOrder);
        }
        if(!limitOrder.isPostOnly()) {
            tradeOnMatchingLimitOrders();
        }

        return limitOrder;
    }

    public List<Trade> tradeOnMatchingLimitOrders() {
        boolean matchingOrders = true;

        List<Trade> tradesOccured = new ArrayList<>();

        while(matchingOrders) {
            LimitOrder highestBid = bids.peek();
            LimitOrder lowestAsk = asks.peek();

            if(highestBid.getPrice() >= lowestAsk.getPrice()) {
                tradesOccured.add(Trade.forOrder(highestBid));
                tradesOccured.add(Trade.forOrder(lowestAsk));
                bids.remove();
                asks.remove();
            } else {
                matchingOrders = false;
            }
        }

        return tradesOccured;
    }
}
