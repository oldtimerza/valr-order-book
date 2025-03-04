package com.valr.orderbook.domain;

import com.valr.orderbook.domain.order.AsksQueue;
import com.valr.orderbook.domain.order.BidsQueue;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.trade.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestOrderBook {
    private final CurrencyPair orderBookCurrencyPair = CurrencyPair.BTCZAR;
    private OrderBook orderBook;

    @BeforeEach
    public void setup() {
        PriorityQueue<LimitOrder> asksQueue = new AsksQueue();
        PriorityQueue<LimitOrder> bidsQueue = new BidsQueue();
        orderBook = new OrderBook(orderBookCurrencyPair, asksQueue, bidsQueue) ;
    }

    @Test
    @DisplayName("Should place sell limit order in asks")
    public void shouldAddSellLimitOrder() {
        LimitOrder sellOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(1000)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.5))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        orderBook.placeLimitOrder(sellOrder);

        assertEquals(1, orderBook.getAsks().size());
        assertEquals(sellOrder, orderBook.getAsks().peek());
    }

    @Test
    @DisplayName("Should place buy limit order in bids")
    public void shouldAddBuyLimitOrder() {
        LimitOrder buyOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.BUY)
                .price(1000)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.5))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        orderBook.placeLimitOrder(buyOrder);

        assertEquals(1, orderBook.getBids().size());
        assertEquals(buyOrder, orderBook.getBids().peek());
    }

    @Test
    @DisplayName("Should not allow placing of orders with non matching currency pairs")
    public void shouldNotAllowOrdersOfNonMatchingCurrencyPair() {
        LimitOrder sellOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(1000)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.5))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(CurrencyPair.ETHUSD)
                .build();

        assertThrows(InvalidCurrencyPairException.class, () -> {
            orderBook.placeLimitOrder(sellOrder);
        });
    }

    @Test
    @DisplayName("Should match equal matching bid to ask")
    public void shouldMatchBidToAsk() {
        final int price = 1000;
        final BigDecimal quantity = BigDecimal.valueOf(0.5);
        LimitOrder sellOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(price)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(quantity)
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        LimitOrder matchingBuyOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.BUY)
                .price(price)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(quantity)
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        orderBook.placeLimitOrder(sellOrder);
        orderBook.placeLimitOrder(matchingBuyOrder);
        List<Trade> trades = orderBook.tradeOnMatchingLimitOrders();

        assertEquals(2, trades.size());
        assertEquals(0, orderBook.getAsks().size());
        assertEquals(0, orderBook.getBids().size());
        Trade expectedSellTrade = new Trade(
                price,
                quantity,
                orderBookCurrencyPair,
                LocalDateTime.now(),
                BuySellSide.SELL,
                UUID.randomUUID(),
                quantity.multiply(BigDecimal.valueOf(price))
        );
        Trade expectedBuyTrade = new Trade(
                price,
                quantity,
                orderBookCurrencyPair,
                LocalDateTime.now(),
                BuySellSide.BUY,
                UUID.randomUUID(),
                quantity.multiply(BigDecimal.valueOf(price))
        );
        assertTradesEqual(expectedSellTrade, trades.get(0));
        assertTradesEqual(expectedBuyTrade, trades.get(1));
    }

    private static void assertTradesEqual(Trade expectedTrade, Trade actualTrade) {
        assertEquals(expectedTrade.getPrice(), actualTrade.getPrice());
        assertEquals(expectedTrade.getQuantity(), actualTrade.getQuantity());
        assertEquals(expectedTrade.getCurrencyPair(), actualTrade.getCurrencyPair());
        assertEquals(expectedTrade.getTakerSide(), actualTrade.getTakerSide());
        assertEquals(expectedTrade.getQuoteVolume(), actualTrade.getQuoteVolume());
    }
}
