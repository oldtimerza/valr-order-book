package com.valr.orderbook.infrastructure.persistence;

import com.valr.orderbook.domain.*;
import com.valr.orderbook.domain.order.AsksQueue;
import com.valr.orderbook.domain.order.BidsQueue;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Consumer;

public class SimpleInMemoryOrderBookRepository implements OrderBookRepository {

    private OrderBook orderBook;

    public SimpleInMemoryOrderBookRepository() {
        //To emulate having data in database already
        PriorityQueue<LimitOrder> asks = new AsksQueue();

        PriorityQueue<LimitOrder> bids = new BidsQueue();

        LimitOrder sellOrder = new LimitOrder.Builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .allowMargin(false)
                .currencyPair(CurrencyPair.BTCZAR)
                .customerOrderId("MyCustomOrder")
                .orderCount(1)
                .postOnly(false)
                .price(10000)
                .side(BuySellSide.SELL)
                .quantity(BigDecimal.valueOf(0.100))
                .reduceOnly(false)
                .timeInForce(TimeInForce.GoodTillCancelled)
                .build();

        LimitOrder buyOrder = new LimitOrder.Builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .allowMargin(false)
                .currencyPair(CurrencyPair.BTCZAR)
                .customerOrderId("MyCustomOrder")
                .orderCount(1)
                .postOnly(false)
                .price(10050)
                .side(BuySellSide.BUY)
                .quantity(BigDecimal.valueOf(0.100))
                .reduceOnly(false)
                .timeInForce(TimeInForce.GoodTillCancelled)
                .build();

        asks.add(sellOrder);

        bids.add(buyOrder);

        this.orderBook = new OrderBook(CurrencyPair.BTCZAR, asks, bids);
        this.orderBook.setSequenceNumber(123456);
        this.orderBook.setLastChange(LocalDateTime.now());
    }

    @Override
    public void fetchOrderBookForCurrencyPairAsync(CurrencyPair currencyPair, Consumer<OrderBook> onComplete, Consumer<Exception> onError) {
        onComplete.accept(orderBook);
    }

    @Override
    public void save(OrderBook orderBook, Consumer<Exception> onError) {
        this.orderBook = orderBook;
        this.orderBook.setLastChange(LocalDateTime.now());
    }
}
