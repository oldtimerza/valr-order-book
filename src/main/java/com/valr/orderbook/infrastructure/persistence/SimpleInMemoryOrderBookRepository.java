package com.valr.orderbook.infrastructure.persistence;

import com.valr.orderbook.domain.*;
import com.valr.orderbook.domain.order.AsksQueue;
import com.valr.orderbook.domain.order.BidsQueue;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Consumer;

public class SimpleInMemoryOrderBookRepository implements OrderBookRepository {

    private Map<CurrencyPair, OrderBook> orderbooksMap = new HashMap<>();

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
                .timeInForce(TimeInForce.GTC)
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
                .timeInForce(TimeInForce.GTC)
                .build();

        asks.add(sellOrder);

        bids.add(buyOrder);

        OrderBook btczarOrderBook = new OrderBook(CurrencyPair.BTCZAR, asks, bids);
        btczarOrderBook.setSequenceNumber(123456);
        btczarOrderBook.setLastChange(LocalDateTime.now());

        OrderBook ethusdOrderBook = new OrderBook(CurrencyPair.ETHUSD, new AsksQueue(), new BidsQueue());
        OrderBook btcusdcOrderBook = new OrderBook(CurrencyPair.BTCUSDC, new AsksQueue(), new BidsQueue());
        OrderBook btcusdOrderBook = new OrderBook(CurrencyPair.BTCUSD, new AsksQueue(), new BidsQueue());
        this.orderbooksMap.put(CurrencyPair.BTCZAR, btczarOrderBook);
        this.orderbooksMap.put(CurrencyPair.ETHUSD, ethusdOrderBook);
        this.orderbooksMap.put(CurrencyPair.BTCUSDC, btcusdcOrderBook);
        this.orderbooksMap.put(CurrencyPair.BTCUSD, btcusdOrderBook);
    }

    @Override
    public void fetchOrderBookForCurrencyPairAsync(CurrencyPair currencyPair, Consumer<OrderBook> onComplete, Consumer<Exception> onError) {
        onComplete.accept(this.orderbooksMap.get(currencyPair));
    }

    @Override
    public void save(OrderBook orderBook, Consumer<Exception> onError) {
        orderBook.setLastChange(LocalDateTime.now());
        this.orderbooksMap.put(orderBook.getCurrencyPair(), orderBook);
    }
}
