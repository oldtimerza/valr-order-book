package com.valr.orderbook.application;

import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.OrderBookRepository;
import com.valr.orderbook.domain.trade.TradeRepository;
import com.valr.orderbook.domain.trade.Trade;

import java.util.List;
import java.util.function.Consumer;

public class TradeService {
    private final OrderBookRepository orderBookRepository;

    private final TradeRepository tradeRepository;

    public TradeService(OrderBookRepository orderBookRepository, TradeRepository tradeRepository) {
        this.orderBookRepository = orderBookRepository;
        this.tradeRepository = tradeRepository;
    }

    public void tradeMatchingOrdersForCurrencyPairAsync(CurrencyPair currencyPair, Consumer<List<Trade>> onComplete, Consumer<Exception> onError) {
        orderBookRepository.fetchOrderBookForCurrencyPairAsync(currencyPair,
                orderBook -> {
                    List<Trade> trades = orderBook.tradeOnMatchingLimitOrders();
                    tradeRepository.saveTrades(trades);
                    onComplete.accept(trades);
                },
                onError
                );
    }

    public void fetchTradeHistoryAsync(CurrencyPair currencyPair, Consumer<List<Trade>> onComplete, Consumer<Exception> onError) {
        tradeRepository.fetchTrades(currencyPair, onComplete, onError);
    }
}
