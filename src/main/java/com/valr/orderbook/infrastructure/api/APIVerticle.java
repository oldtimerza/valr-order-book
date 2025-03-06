package com.valr.orderbook.infrastructure.api;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookForCurrencyPairNotFound;
import com.valr.orderbook.infrastructure.api.request.CreateLimitOrderRequest;
import com.valr.orderbook.infrastructure.api.response.AnonymousOrderBookData;
import com.valr.orderbook.infrastructure.api.response.ErrorCode;
import com.valr.orderbook.infrastructure.api.response.ErrorResponse;
import com.valr.orderbook.infrastructure.api.response.LimitOrderIdOnly;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.application.OrderMatchingService;
import com.valr.orderbook.domain.trade.Trade;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class APIVerticle extends AbstractVerticle {
  private final static String API_VERSION = "v1";
  private final OrderMatchingService orderMatchingService;

  public APIVerticle(final OrderMatchingService orderMatchingService) {
    this.orderMatchingService = orderMatchingService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    DatabindCodec.mapper().registerModule(new JavaTimeModule());

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.route(HttpMethod.GET, "/:currencypair/orderbook")
            .respond(this::fetchOrderBook)
            .failureHandler(this::handleError);

    router.route(HttpMethod.POST, "/"+ API_VERSION +"/orders/limit")
            .handler(BodyHandler.create().setBodyLimit(10000))
            .respond(this::createLimitOrder)
            .failureHandler(this::handleError);

    router.route(HttpMethod.GET,  "/:currencypair/tradehistory")
            .respond(this::fetchTradeHistory)
            .failureHandler(this::handleError);

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
    orderMatchingService.fetchOrderBookAsync(currencyPair, promiseOrderBook::complete, promiseOrderBook::fail);

    return promiseOrderBook
            .future()
            .map(AnonymousOrderBookData::from)
            .map(JsonObject::mapFrom);
  }

  private Future<JsonObject> createLimitOrder(RoutingContext routingContext) {
    JsonObject requestJson = routingContext.body().asJsonObject();
    CreateLimitOrderRequest createLimitOrderRequest = requestJson.mapTo(CreateLimitOrderRequest.class);

    LimitOrder limitOrder = new LimitOrder.Builder()
            .side(createLimitOrderRequest.getSide())
            .quantity(createLimitOrderRequest.getQuantity())
            .price(createLimitOrderRequest.getPrice())
            .currencyPair(createLimitOrderRequest.getCurrencyPair())
            .postOnly(createLimitOrderRequest.isPostOnly())
            .customerOrderId(createLimitOrderRequest.getCustomerOrderId())
            .timeInForce(createLimitOrderRequest.getTimeInForce())
            .allowMargin(createLimitOrderRequest.isAllowMargin())
            .reduceOnly(createLimitOrderRequest.isReduceOnly())
            .build();

    Promise<LimitOrder> promiseLimitOrderCreation = Promise.promise();

    orderMatchingService.createLimitOrderAsync(limitOrder, promiseLimitOrderCreation::complete, promiseLimitOrderCreation::fail);

    return promiseLimitOrderCreation
            .future()
            .map(LimitOrderIdOnly::from)
            .map(JsonObject::mapFrom);
  }

  private Future<JsonArray> fetchTradeHistory(RoutingContext routingContext) {
    final String currencyPairQueryParam = routingContext.pathParam("currencypair");
    final CurrencyPair currencyPair = CurrencyPair.valueOf(currencyPairQueryParam);

    Promise<List<Trade>> promiseTradeHistory = Promise.promise();

    orderMatchingService.fetchTradeHistoryAsync(currencyPair, promiseTradeHistory::complete, promiseTradeHistory::fail);

    return promiseTradeHistory
            .future()
            .map(JsonArray::of);
  }

  private void handleError(RoutingContext failureRoutingContext) {
      HttpServerResponse response = failureRoutingContext.response();

      if (failureRoutingContext.failure() instanceof OrderBookForCurrencyPairNotFound) {
        ErrorResponse errorResponse = new ErrorResponse(failureRoutingContext.failure().toString(), ErrorCode.E001);
        response.setStatusCode(404)
                .end(JsonObject.mapFrom(errorResponse).toString());
        return;
      }

      if(failureRoutingContext.failure() instanceof InvalidCurrencyPairException) {
        ErrorResponse errorResponse = new ErrorResponse(failureRoutingContext.failure().toString(), ErrorCode.E002);
        response.setStatusCode(400)
                .end(JsonObject.mapFrom(errorResponse).toString());
        return;
      }
  }
}
