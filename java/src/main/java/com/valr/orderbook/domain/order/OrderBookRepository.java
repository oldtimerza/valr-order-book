package com.valr.orderbook.domain.order;

import com.valr.orderbook.domain.CurrencyPair;

import java.util.function.Consumer;

public interface OrderBookRepository {
    void fetchOrderBookForCurrencyPairAsync(CurrencyPair currencyPair, Consumer<OrderBook> onComplete, Consumer<Exception> onError);

    void save(OrderBook orderBook);
}
