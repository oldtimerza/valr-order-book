package com.valr.orderbook;

import com.valr.orderbook.domain.trade.TradeRepository;
import com.valr.orderbook.infrastructure.api.APIVerticle;
import com.valr.orderbook.application.OrderMatchingService;
import com.valr.orderbook.domain.order.OrderBookRepository;
import com.valr.orderbook.infrastructure.persistence.SimpleInMemoryOrderBookRepository;
import com.valr.orderbook.infrastructure.persistence.SimpleInMemoryTradeRepository;
import io.vertx.core.Vertx;

import java.util.ArrayList;

public class OrderBookApplication {
    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        TradeRepository tradeRepository = new SimpleInMemoryTradeRepository(new ArrayList<>());
        OrderBookRepository orderBookRepository = new SimpleInMemoryOrderBookRepository();
        OrderMatchingService orderMatchingService = new OrderMatchingService(orderBookRepository, tradeRepository);

        APIVerticle apiVerticle = new APIVerticle(orderMatchingService);

        vertx.deployVerticle(apiVerticle);
    }
}
