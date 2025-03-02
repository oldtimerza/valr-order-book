package com.valr.orderbook.infrastructure.persistence;

import com.valr.orderbook.domain.*;
import com.valr.orderbook.domain.order.AsksQueue;
import com.valr.orderbook.domain.order.BidsQueue;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookRepository;
import com.valr.orderbook.domain.trade.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;

public class SimpleInMemoryOrderBookRepository implements OrderBookRepository {

    private OrderBook orderBook;

    public SimpleInMemoryOrderBookRepository() {
        PriorityQueue<LimitOrder> asks = new AsksQueue();

        PriorityQueue<LimitOrder> bids = new BidsQueue();

        List<Trade> trades = new ArrayList<>();

        LimitOrder sellOrder = new LimitOrder( BuySellSide.SELL, BigDecimal.valueOf(0.02), 123456, "BTCZAR", 1));

        LimitOrder buyOrder = new LimitOrder( BuySellSide.BUY, BigDecimal.valueOf(0.02), 123456, "BTCZAR", 1));

        asks.add(sellOrder);

        bids.add(buyOrder);

        orderBook = new OrderBook(asks, bids, trades);
    }

    @Override
    public void fetchOrderBookForCurrencyPairAsync(CurrencyPair currencyPair, Consumer<OrderBook> onComplete, Consumer<Exception> onError) {
        onComplete.accept(orderBook);
    }
}
