package com.valr.orderbook.infrastructure.api;

import com.valr.orderbook.application.OrderMatchingService;
import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.domain.TimeInForce;
import com.valr.orderbook.domain.order.*;
import com.valr.orderbook.domain.trade.Trade;
import com.valr.orderbook.domain.trade.TradeRepository;
import com.valr.orderbook.infrastructure.api.request.CreateLimitOrderRequest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private OrderMatchingService mockOrderMatchingService;
    private TradeRepository mockTradeRepository;

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext testContext) {
        mockOrderMatchingService = mock(OrderMatchingService.class);
        mockTradeRepository = mock(TradeRepository.class);

        vertx.deployVerticle(new APIVerticle(mockOrderMatchingService))
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
        }).when(mockOrderMatchingService).fetchOrderBookAsync(eq(CurrencyPair.BTCZAR), any(), any());

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
        doThrow(InvalidCurrencyPairException.class).when(mockOrderMatchingService).fetchOrderBookAsync(eq(CurrencyPair.ETHUSD), any(), any());

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
        doThrow(OrderBookForCurrencyPairNotFound.class).when(mockOrderMatchingService).fetchOrderBookAsync(eq(CurrencyPair.ETHUSD), any(), any());

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

    @Test
    @DisplayName("GET /:currencypair/tradehistory - should retrieve tradehistory")
    public void shouldRetrieveTradeHistoryForCurrencyPair(Vertx vertx, VertxTestContext testContext) {
        List<Trade> trades = new ArrayList<>() {{
            add(new Trade(
                    100,
                    BigDecimal.valueOf(0.05),
                    CurrencyPair.BTCZAR,
                    LocalDateTime.of(2021, 02, 03, 12, 00),
                    BuySellSide.SELL,
                    UUID.fromString("4f914ae1-ff7b-4670-a05b-34e737ccbccc"),
                    BigDecimal.valueOf(5)
            ));
        }};
        doAnswer((Answer<Void>) invocation -> {
            Consumer<List<Trade>> callback = invocation.getArgument(1);
            callback.accept(trades);
            return null;
        }).when(mockOrderMatchingService).fetchTradeHistoryAsync(eq(CurrencyPair.BTCZAR), any(), any());

        final String expectedJsonResponse = "[[{\"price\":100,\"quantity\":0.05,\"currencyPair\":\"BTCZAR\",\"tradedAt\":[2021,2,3,12,0],\"takerSide\":\"SELL\",\"id\":\"4f914ae1-ff7b-4670-a05b-34e737ccbccc\",\"quoteVolume\":5}]]";
        vertx.createHttpClient()
                .request(HttpMethod.GET, PORT, HOST, "/BTCZAR/tradehistory")
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
    @DisplayName("POST /v1/orders/limit - should create limit order")
    public void shouldCreateLimitOrder(Vertx vertx, VertxTestContext testContext) {
        CreateLimitOrderRequest createLimitOrderRequest = new CreateLimitOrderRequest(
                BuySellSide.SELL,
                BigDecimal.valueOf(0.5),
                100,
                CurrencyPair.BTCZAR,
                false,
                "someCustomId",
                TimeInForce.ImmediateOrCancel,
                false,
                false
        );
        final UUID id = UUID.randomUUID();
        LimitOrder limitOrder = new LimitOrder.Builder()
                .id(id)
                .side(createLimitOrderRequest.getSide())
                .quantity(createLimitOrderRequest.getQuantity())
                .price(createLimitOrderRequest.getPrice())
                .postOnly(createLimitOrderRequest.isPostOnly())
                .customerOrderId(createLimitOrderRequest.getCustomerOrderId())
                .timeInForce(createLimitOrderRequest.getTimeInForce())
                .allowMargin(createLimitOrderRequest.isAllowMargin())
                .reduceOnly(createLimitOrderRequest.isReduceOnly())
                .createdAt(LocalDateTime.now())
                .build();
        ArgumentCaptor<LimitOrder> limitOrderArgumentCaptor = ArgumentCaptor.forClass(LimitOrder.class);
        doAnswer((Answer<Void>) invocation -> {
            Consumer<LimitOrder> callback = invocation.getArgument(1);
            callback.accept(limitOrder);
            return null;
        }).when(mockOrderMatchingService).createLimitOrderAsync(limitOrderArgumentCaptor.capture(), any(), any());

        final String expectedJsonResponse = "{\"id\":\""+ id +"\"}";
        vertx.createHttpClient()
                .request(HttpMethod.POST, PORT, HOST, "/v1/orders/limit")
                .compose(request -> request.send(JsonObject.mapFrom(createLimitOrderRequest).toString()))
                .compose(HttpClientResponse::body)
                .onSuccess(body -> testContext.verify(() -> {
                    // Check the response
                    assertEquals(expectedJsonResponse, body.toString());
                    final LimitOrder capturedLimitOrder = limitOrderArgumentCaptor.getValue();
                    assertEquals(limitOrder.getSide(), capturedLimitOrder.getSide());
                    assertEquals(limitOrder.getQuantity(), capturedLimitOrder.getQuantity());
                    assertEquals(limitOrder.getPrice(), capturedLimitOrder.getPrice());
                    assertEquals(limitOrder.isPostOnly(), capturedLimitOrder.isPostOnly());
                    assertEquals(limitOrder.getCustomerOrderId(), capturedLimitOrder.getCustomerOrderId());
                    assertEquals(limitOrder.getTimeInForce(), capturedLimitOrder.getTimeInForce());
                    assertEquals(limitOrder.isAllowMargin(), capturedLimitOrder.isAllowMargin());
                    assertEquals(limitOrder.isReduceOnly(), capturedLimitOrder.isReduceOnly());
                    testContext.completeNow();
                }))
                .onFailure(testContext::failNow);
    }
}
