package com.valr.orderbook.application;

import com.valr.orderbook.domain.order.LimitOrder;
import com.valr.orderbook.domain.order.OrderBook;
import com.valr.orderbook.domain.order.OrderBookRepository;
import io.vertx.core.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestOrderBookService {

    private OrderBookRepository mockOrderBookRepository;
    private OrderBookService orderBookService;

    @BeforeEach
    public void setup() {
        mockOrderBookRepository = mock(OrderBookRepository.class);
        orderBookService = new OrderBookService(mockOrderBookRepository);
    }

    @Test
    public void shouldFetchOrderBookAsync() {
        final String currencyPair = "BTCZAR";
        List<LimitOrder> asks = new ArrayList<>();
        List<LimitOrder> bids = new ArrayList<>();
        final OrderBook orderBook = new OrderBook(asks, bids);
        final Future<OrderBook> orderBookFuture = Future.succeededFuture(orderBook);
        when(mockOrderBookRepository.fetchOrderBookForCurrencyPair(eq("BTCZAR"))).thenReturn(orderBookFuture);

        final OrderBook actualOrderBook = orderBookService.fetchOrderBookAsync(currencyPair).result();

        assertEquals(orderBook, actualOrderBook);
    }
}
