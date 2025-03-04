package com.valr.orderbook.application;

import com.valr.orderbook.domain.BuySellSide;
import com.valr.orderbook.domain.CurrencyPair;
import com.valr.orderbook.domain.TimeInForce;
import com.valr.orderbook.domain.order.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TestOrderBookService {

    private OrderBookRepository mockOrderBookRepository;
    private OrderBookService orderBookService;

    @BeforeEach
    public void setup() {
        mockOrderBookRepository = mock(OrderBookRepository.class);
        orderBookService = new OrderBookService(mockOrderBookRepository);
    }

    @Test
    @DisplayName("Should successfully fetch the orderbook for a given currency pair.")
    public void shouldFetchOrderBookAsync() {
        PriorityQueue<LimitOrder> asks = new AsksQueue();
        PriorityQueue<LimitOrder> bids = new BidsQueue();
        final CurrencyPair currencyPair = CurrencyPair.BTCZAR;
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
        doAnswer((Answer<Void>) invocation -> {
            Consumer<OrderBook> callback = invocation.getArgument(1);
            callback.accept(orderBook);
            return null;
        }).when(mockOrderBookRepository).fetchOrderBookForCurrencyPairAsync(eq(CurrencyPair.BTCZAR), any(), any());

        orderBookService.fetchOrderBookAsync(currencyPair,
                actualOrderBook -> {
                    assertEquals(orderBook, actualOrderBook);
                },
                ex -> {
                    Assertions.fail();
                }
        );
    }
}
