package com.valr.orderbook.infrastructure;

import com.valr.orderbook.application.OrderBookService;
import com.valr.orderbook.application.TradeService;
import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.domain.TimeInForce;
import com.valr.orderbook.domain.order.*;
import com.valr.orderbook.infrastructure.api.APIVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
public class TestAPIVerticle {
    private final static int PORT = 8888;
    private final static String HOST = "127.0.0.1";

    private OrderBookService mockOrderBookService;
    private TradeService mockTradeService;

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext testContext) {
        mockOrderBookService = mock(OrderBookService.class);
        mockTradeService = mock(TradeService.class);

        vertx.deployVerticle(new APIVerticle(mockOrderBookService, mockTradeService))
                .onSuccess(ok -> testContext.completeNow())
                .onFailure(testContext::failNow);
    }

    @Test
    @DisplayName("GET /:currencypair/orderbook - returns successful orderbook for BTCZAR")
    public void shouldReturnOrderBookForCurrencyPair(Vertx vertx, VertxTestContext testContext) {
        CurrencyPair currencyPair = CurrencyPair.BTCZAR;
        PriorityQueue<LimitOrder> asks = new AsksQueue();
        PriorityQueue<LimitOrder> bids = new BidsQueue();

        LimitOrder sellOrder = new LimitOrder.Builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .allowMargin(false)
                .currencyPair(CurrencyPair.BTCZAR)
                .customerOrderId("MyCustomOrder")
                .orderCount(1)
                .postOnly(false)
                .price(10000)
                .side(BuySellSide.SELL)
                .quantity(BigDecimal.valueOf(0.100))
                .reduceOnly(false)
                .timeInForce(TimeInForce.GoodTillCancelled)
                .build();

        LimitOrder buyOrder = new LimitOrder.Builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .allowMargin(false)
                .currencyPair(CurrencyPair.BTCZAR)
                .customerOrderId("MyCustomOrder")
                .orderCount(1)
                .postOnly(false)
                .price(10050)
                .side(BuySellSide.BUY)
                .quantity(BigDecimal.valueOf(0.100))
                .reduceOnly(false)
                .timeInForce(TimeInForce.GoodTillCancelled)
                .build();

        asks.add(sellOrder);
        bids.add(buyOrder);

        OrderBook orderBook = new OrderBook(currencyPair, asks, bids);
        orderBook.setSequenceNumber(1);
        LocalDateTime currentDate = LocalDateTime.of(2025, 2, 28, 12, 50);
        orderBook.setLastChange(currentDate);
        doAnswer((Answer<Void>) invocation -> {
            Consumer<OrderBook> callback = invocation.getArgument(1);
            callback.accept(orderBook);
            return null;
        }).when(mockOrderBookService).fetchOrderBookAsync(eq(CurrencyPair.BTCZAR), any(), any());

        final String expectedJsonResponse = "{\"asks\":[{\"side\":\"SELL\",\"quantity\":0.1,\"price\":10000,\"currencyPair\":\"BTCZAR\",\"orderCount\":1}],\"bids\":[{\"side\":\"BUY\",\"quantity\":0.1,\"price\":10050,\"currencyPair\":\"BTCZAR\",\"orderCount\":1}],\"sequenceNumber\":1,\"lastChanged\":\"2025-02-28T12:50:00\"}";
        vertx.createHttpClient()
                .request(HttpMethod.GET, PORT, HOST, "/BTCZAR/orderbook")
                .compose(HttpClientRequest::send)
                .compose(HttpClientResponse::body)
                .onSuccess(body -> testContext.verify(() -> {
                    // Check the response
                    assertEquals(expectedJsonResponse, body.toString());
                    testContext.completeNow();
                }))
                .onFailure(testContext::failNow);
    }

    @Test
    @DisplayName("GET /:currencypair/orderbook - returns an error message for 400 response when failing to fetch orderbook for non-existant currencypair")
    public void shouldReturnErrorWhenCurrencyPairInvalid(Vertx vertx, VertxTestContext testContext) {
        doThrow(InvalidCurrencyPairException.class).when(mockOrderBookService).fetchOrderBookAsync(eq(CurrencyPair.ETHUSD), any(), any());

        vertx.createHttpClient()
                .request(HttpMethod.GET, PORT, HOST, "/ETHUSD/orderbook")
                .compose(HttpClientRequest::send)
                .onSuccess(response -> testContext.verify(() -> {
                    final int statusCode = response.statusCode();
                    // Check the response
                    assertEquals(400, statusCode);
                    testContext.completeNow();
                }))
                .onFailure(testContext::failNow);
    }

    @Test
    @DisplayName("GET /:currencypair/orderbook - returns an error message for 404 response when orderbook does not exist")
    public void shouldReturn404ErrorWhenOrderBookDoesNotExist(Vertx vertx, VertxTestContext testContext) {
        doThrow(OrderBookForCurrencyPairNotFound.class).when(mockOrderBookService).fetchOrderBookAsync(eq(CurrencyPair.ETHUSD), any(), any());

        vertx.createHttpClient()
                .request(HttpMethod.GET, PORT, HOST, "/ETHUSD/orderbook")
                .compose(HttpClientRequest::send)
                .onSuccess(response -> testContext.verify(() -> {
                    final int statusCode = response.statusCode();
                    // Check the response
                    assertEquals(404, statusCode);
                    testContext.completeNow();
                }))
                .onFailure(testContext::failNow);
    }
}
