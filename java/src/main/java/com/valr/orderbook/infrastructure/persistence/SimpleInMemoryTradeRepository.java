package com.valr.orderbook.infrastructure.persistence;

import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.trade.Trade;
import com.valr.orderbook.domain.trade.TradeRepository;

import java.util.List;
import java.util.function.Consumer;

public class SimpleInMemoryTradeRepository implements TradeRepository {
    private final List<Trade> trades;

    public SimpleInMemoryTradeRepository(List<Trade> trades) {
        this.trades = trades;
    }

    @Override
    public void saveTrades(List<Trade> trades) {
        this.trades.addAll(trades);
    }

    @Override
    public void fetchTrades(CurrencyPair currencyPair, Consumer<List<Trade>> onComplete, Consumer<Exception> onError) {
        onComplete.accept(trades);
    }
}
