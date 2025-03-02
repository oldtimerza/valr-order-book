package com.valr.orderbook.infrastructure;

import com.valr.orderbook.application.OrderBookService;
import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.InvalidCurrencyPairException;
import com.valr.orderbook.infrastructure.api.APIVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(VertxExtension.class)
public class TestAPIVerticle {
    private final static int PORT = 8888;
    private final static String HOST = "127.0.0.1";

    private OrderBookService mockOrderBookService;

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext testContext) {
        mockOrderBookService = mock(OrderBookService.class);

        vertx.deployVerticle(new APIVerticle(mockOrderBookService))
                .onSuccess(ok -> testContext.completeNow())
                .onFailure(failure -> testContext.failNow(failure));
    }

    @Test
    @DisplayName("GET /:currencypair/orderbook - returns successful orderbook for BTCZAR")
    public void shouldReturnOrderBookForCurrencyPair(Vertx vertx, VertxTestContext testContext) {
        List<LimitOrder> asks = new ArrayList<>() {{
            add(new LimitOrder(BuySellSide.SELL, BigDecimal.valueOf(0.02), 123456, "BTCZAR", 1));
        }};

        List<LimitOrder> bids = new ArrayList<>() {{
            add(new LimitOrder(BuySellSide.BUY, BigDecimal.valueOf(0.02), 123456, "BTCZAR", 1));
        }};
        OrderBook orderBook = new OrderBook(asks, bids);
        when(mockOrderBookService.fetchOrderBookAsync(eq("BTCZAR"))).thenReturn(Future.succeededFuture(orderBook));

        final String expectedJsonResponse = "{\"asks\":[{\"side\":\"SELL\",\"quantity\":0.02,\"price\":123456,\"currencyPair\":\"BTCZAR\",\"orderCount\":1}],\"bids\":[{\"side\":\"BUY\",\"quantity\":0.02,\"price\":123456,\"currencyPair\":\"BTCZAR\",\"orderCount\":1}]}";
        vertx.createHttpClient()
                .request(HttpMethod.GET, PORT, HOST, "/BTCZAR/orderbook")
                .compose(request -> request.send())
                .compose(response -> response.body())
                .onSuccess(body -> testContext.verify(() -> {
                    // Check the response
                    assertEquals(expectedJsonResponse, body.toString());
                    testContext.completeNow();
                }))
                .onFailure(failure -> testContext.failNow(failure));
    }

    @Test
    @DisplayName("GET /:currencypair/orderbook - returns an error message for 400 response when failing to fetch orderbook for non-existant currencypair")
    public void shouldReturnErrorWhenCurrencyPairInvalid(Vertx vertx, VertxTestContext testContext) {
        when(mockOrderBookService.fetchOrderBookAsync(eq("BTCZAR"))).thenThrow(new InvalidCurrencyPairException("BTCZAR"));

        final String expectedJsonResponse = "";
        vertx.createHttpClient()
                .request(HttpMethod.GET, PORT, HOST, "/BTCZAR/orderbook")
                .compose(request -> request.send())
                .onSuccess(response -> testContext.verify(() -> {
                    final int statusCode = response.statusCode();
                    // Check the response
                    assertEquals(400, statusCode);
                    testContext.completeNow();
                }))
                .onFailure(failure -> testContext.failNow(failure));
    }
}
