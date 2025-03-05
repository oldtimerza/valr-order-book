package com.valr.orderbook.application;

import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookRepository;
import com.valr.orderbook.domain.trade.Trade;
import com.valr.orderbook.domain.trade.TradeRepository;

import java.util.List;
import java.util.function.Consumer;

public class OrderMatchingService {

    private final OrderBookRepository orderBookRepository;

    private final TradeRepository tradeRepository;

    public OrderMatchingService(final OrderBookRepository orderBookRepository, TradeRepository tradeRepository) {
        this.orderBookRepository = orderBookRepository;
        this.tradeRepository = tradeRepository;
    }

    public void fetchOrderBookAsync(final CurrencyPair currencyPair, Consumer<OrderBook> onComplete, Consumer<Exception> onError) {
        this.orderBookRepository.fetchOrderBookForCurrencyPairAsync(currencyPair, onComplete, onError);
    }

    public void createLimitOrderAsync(LimitOrder limitOrder, Consumer<LimitOrder> onComplete, Consumer<Exception> onError) {
        this.orderBookRepository
                .fetchOrderBookForCurrencyPairAsync(limitOrder.getCurrencyPair(),
                orderBook -> {
                    orderBook.placeLimitOrder(limitOrder);
                    orderBookRepository.save(orderBook, onError);
                    if(!limitOrder.isPostOnly()) {
                        List<Trade> tradesMade = orderBook.tradeOnMatchingLimitOrders();
                        tradeRepository.saveTrades(tradesMade, onError);
                    }
                    onComplete.accept(limitOrder);
                }, onError);
    }

    public void fetchTradeHistoryAsync(CurrencyPair currencyPair, Consumer<List<Trade>> onComplete, Consumer<Exception> onError) {
        tradeRepository.fetchTrades(currencyPair, onComplete, onError);
    }
}
