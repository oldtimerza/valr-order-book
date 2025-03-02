package com.valr.orderbook;

import com.valr.orderbook.application.TradeService;
import com.valr.orderbook.domain.trade.TradeRepository;
import com.valr.orderbook.infrastructure.api.APIVerticle;
import com.valr.orderbook.application.OrderBookService;
import com.valr.orderbook.domain.order.OrderBookRepository;
import com.valr.orderbook.infrastructure.persistence.SimpleInMemoryOrderBookRepository;
import com.valr.orderbook.infrastructure.persistence.SimpleInMemoryTradeRepository;
import io.vertx.core.Vertx;

import java.util.ArrayList;

public class OrderBookApplication {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        OrderBookRepository orderBookRepository = new SimpleInMemoryOrderBookRepository();
        OrderBookService orderBookService = new OrderBookService(orderBookRepository);
        TradeRepository tradeRepository = new SimpleInMemoryTradeRepository(new ArrayList<>());
        TradeService tradeService = new TradeService(orderBookRepository, tradeRepository);
        APIVerticle apiVerticle = new APIVerticle(orderBookService, tradeService);

        vertx.deployVerticle(apiVerticle);
    }
}
