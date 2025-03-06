package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.domain.TimeInForce;
import com.valr.orderbook.domain.trade.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        assertTradeEqual(expectedSellTrade, trades.get(0));
        assertTradeEqual(expectedBuyTrade, trades.get(1));
    }

    @Test
    @DisplayName("Should fulfill Bid with as many asks as possible")
    public void shouldMatchAsMuchOfBidWithAvailableAsks() {
        final int buyprice = 1000;
        final int lowestSellPrice = 700;
        final int midSellPrice = 800;
        final int highestSellPrice = 900;
        final BigDecimal totalQuantity = BigDecimal.ONE;
        LimitOrder lowestAsk = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(lowestSellPrice)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.3))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        LimitOrder midAsk = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(midSellPrice)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.3))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        LimitOrder highestAsk = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(highestSellPrice)
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

        LimitOrder bid = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.BUY)
                .price(buyprice)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(totalQuantity)
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        orderBook.placeLimitOrder(lowestAsk);
        orderBook.placeLimitOrder(midAsk);
        orderBook.placeLimitOrder(highestAsk);
        orderBook.placeLimitOrder(bid);
        List<Trade> trades = orderBook.tradeOnMatchingLimitOrders();

        final BigDecimal remainingQuantityFromBidForHighestAsk = BigDecimal.valueOf(0.4);
        List<Trade> expectedTrades = new ArrayList<>() {{
            add(new Trade(
                    lowestAsk.getPrice(),
                    lowestAsk.getQuantity(),
                    lowestAsk.getCurrencyPair(),
                    lowestAsk.getCreatedAt(),
                    BuySellSide.SELL,
                    lowestAsk.getId(),
                    lowestAsk.getQuantity().multiply(BigDecimal.valueOf(lowestAsk.getPrice()))
            ));
            add(new Trade(
                    lowestAsk.getPrice(),
                    lowestAsk.getQuantity(),
                    lowestAsk.getCurrencyPair(),
                    lowestAsk.getCreatedAt(),
                    BuySellSide.BUY,
                    lowestAsk.getId(),
                    lowestAsk.getQuantity().multiply(BigDecimal.valueOf(lowestAsk.getPrice()))
            ));
            add(new Trade(
                    midAsk.getPrice(),
                    midAsk.getQuantity(),
                    midAsk.getCurrencyPair(),
                    midAsk.getCreatedAt(),
                    BuySellSide.SELL,
                    midAsk.getId(),
                    midAsk.getQuantity().multiply(BigDecimal.valueOf(midAsk.getPrice()))
            ));
            add(new Trade(
                    midAsk.getPrice(),
                    midAsk.getQuantity(),
                    midAsk.getCurrencyPair(),
                    midAsk.getCreatedAt(),
                    BuySellSide.BUY,
                    midAsk.getId(),
                    midAsk.getQuantity().multiply(BigDecimal.valueOf(midAsk.getPrice()))
            ));
            add(new Trade(
                    highestAsk.getPrice(),
                    remainingQuantityFromBidForHighestAsk,
                    highestAsk.getCurrencyPair(),
                    highestAsk.getCreatedAt(),
                    BuySellSide.SELL,
                    highestAsk.getId(),
                    remainingQuantityFromBidForHighestAsk.multiply(BigDecimal.valueOf(highestAsk.getPrice()))
            ));
            add(new Trade(
                    highestAsk.getPrice(),
                    remainingQuantityFromBidForHighestAsk,
                    highestAsk.getCurrencyPair(),
                    highestAsk.getCreatedAt(),
                    BuySellSide.BUY,
                    highestAsk.getId(),
                    remainingQuantityFromBidForHighestAsk.multiply(BigDecimal.valueOf(highestAsk.getPrice()))
            ));
        }};
        assertTradesEqual(expectedTrades, trades);
        assertEquals(1, orderBook.getAsks().size());
        assertEquals(0, orderBook.getBids().size());
    }

    @Test
    @DisplayName("Should not match any trades if bid and ask don't match")
    public void shouldNotMatchOnNonMatchingBidsAndAsks() {
        final int buyprice = 900;
        final int sellPrice = 1000;
        LimitOrder missingSellOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.SELL)
                .price(sellPrice)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.3))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        LimitOrder missingBuyOrder = new LimitOrder.Builder()
                .timeInForce(TimeInForce.ImmediateOrCancel)
                .side(BuySellSide.BUY)
                .price(buyprice)
                .reduceOnly(false)
                .id(UUID.randomUUID())
                .quantity(BigDecimal.valueOf(0.3))
                .orderCount(1)
                .postOnly(true)
                .customerOrderId("someId")
                .allowMargin(true)
                .createdAt(LocalDateTime.now())
                .currencyPair(orderBookCurrencyPair)
                .build();

        orderBook.placeLimitOrder(missingSellOrder);
        orderBook.placeLimitOrder(missingBuyOrder);
        List<Trade> trades = orderBook.tradeOnMatchingLimitOrders();

        assertEquals(0, trades.size());
    }

    private static void assertTradesEqual(List<Trade> expectedTrades, List<Trade> actualTrades) {
        assertNotNull(expectedTrades);
        assertNotNull(actualTrades);
        assertEquals(expectedTrades.size(), actualTrades.size());
        for(int i = 0; i < expectedTrades.size(); i++) {
            assertTradeEqual(expectedTrades.get(i), actualTrades.get(i));
        }
    }

    private static void assertTradeEqual(Trade expectedTrade, Trade actualTrade) {
        assertEquals(expectedTrade.getPrice(), actualTrade.getPrice());
        assertEquals(expectedTrade.getQuantity(), actualTrade.getQuantity());
        assertEquals(expectedTrade.getCurrencyPair(), actualTrade.getCurrencyPair());
        assertEquals(expectedTrade.getTakerSide(), actualTrade.getTakerSide());
        assertEquals(expectedTrade.getQuoteVolume(), actualTrade.getQuoteVolume());
    }
}
