package com.valr.orderbook.application;

import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookRepository;

import java.util.function.Consumer;

public class OrderBookService {

    private final OrderBookRepository orderBookRepository;

    public OrderBookService(final OrderBookRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    public void fetchOrderBookAsync(final CurrencyPair currencyPair, Consumer<OrderBook> onComplete, Consumer<Exception> onError) {
        this.orderBookRepository.fetchOrderBookForCurrencyPairAsync(currencyPair, onComplete, onError);
    }

    public void createLimitOrderAsync(LimitOrder limitOrder, Consumer<LimitOrder> onComplete, Consumer<Exception> onError) {
        this.orderBookRepository
                .fetchOrderBookForCurrencyPairAsync(limitOrder.getCurrencyPair(),
                orderBook -> {
                    orderBook.placeLimitOrder(limitOrder);
                    orderBookRepository.save(orderBook);
                    onComplete.accept(limitOrder);
                }, onError);
    }
}
