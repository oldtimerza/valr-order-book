package com.valr.orderbook.infrastructure.api;

import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.infrastructure.api.response.MinimalLimitOrderResponse;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.application.OrderBookService;
import com.valr.orderbook.domain.trade.Trade;
import com.valr.orderbook.application.TradeService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class APIVerticle extends AbstractVerticle {
  private final static String API_VERSION = "v1";
  private final OrderBookService orderBookService;
  private final TradeService tradeService;

  public APIVerticle(final OrderBookService orderBookService, TradeService tradeService) {
    this.orderBookService = orderBookService;
      this.tradeService = tradeService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.route(HttpMethod.GET, "/:currencypair/orderbook")
          .respond(this::fetchOrderBook);

    router.route(HttpMethod.POST, "/"+ API_VERSION +"/orders/limit")
            .handler(BodyHandler.create().setBodyLimit(10000))
            .respond(this::createLimitOrder);

    router.route(HttpMethod.POST,  "/:currencypair/tradehistory")
            .respond(this::fetchTradeHistory);

    server.requestHandler(router);

    server.listen(8888).onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private Future<JsonObject> fetchOrderBook(RoutingContext routingContext) {
    final String currencyPairQueryParam = routingContext.pathParam("currencypair");
    final CurrencyPair currencyPair = CurrencyPair.valueOf(currencyPairQueryParam);

    Promise<OrderBook> promiseOrderBook = Promise.promise();
    orderBookService.fetchOrderBookAsync(currencyPair, promiseOrderBook::complete, promiseOrderBook::fail);

    return promiseOrderBook.future().map(JsonObject::mapFrom);
  }

  private Future<JsonObject> createLimitOrder(RoutingContext routingContext) {
    JsonObject requestJson = routingContext.body().asJsonObject();
    LimitOrder limitOrder = requestJson.mapTo(LimitOrder.class);

    Promise<LimitOrder> promiseLimitOrderCreation = Promise.promise();

    orderBookService.createLimitOrderAsync(limitOrder, promiseLimitOrderCreation::complete, promiseLimitOrderCreation::fail);

    return promiseLimitOrderCreation
            .future()
            .map(MinimalLimitOrderResponse::from)
            .map(JsonObject::mapFrom);
  }

  private Future<JsonObject> fetchTradeHistory(RoutingContext routingContext) {
    final String currencyPairQueryParam = routingContext.pathParam("currencypair");
    final CurrencyPair currencyPair = CurrencyPair.valueOf(currencyPairQueryParam);

    Promise<List<Trade>> promiseTradeHistory = Promise.promise();

    tradeService.fetchTradeHistoryAsync(currencyPair, promiseTradeHistory::complete, promiseTradeHistory::fail);

    return promiseTradeHistory.future().map(JsonObject::mapFrom);
  }
}
