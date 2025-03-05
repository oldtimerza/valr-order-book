package com.valr.orderbook.domain.trade;

import com.valr.orderbook.domain.CurrencyPair;

import java.util.List;
import java.util.function.Consumer;

public interface TradeRepository {
    void saveTrades(List<Trade> trades, Consumer<Exception> onError);

    void fetchTrades(CurrencyPair currencyPair, Consumer<List<Trade>> onComplete, Consumer<Exception> onError);
}
