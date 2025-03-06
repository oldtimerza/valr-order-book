package com.valr.orderbook.infrastructure.api.response.orderbook;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.valr.orderbook.domain.order.OrderBook;

import java.time.LocalDateTime;
import java.util.List;

public class AnonymousOrderBookData {

    private final List<MinimumLimitOrderData> asks;
    private final List<MinimumLimitOrderData> bids;

    private final int sequenceNumber;
    private final LocalDateTime lastChanged;

    private AnonymousOrderBookData(List<MinimumLimitOrderData> asks, List<MinimumLimitOrderData> bids, int sequenceNumber, LocalDateTime lastChanged) {
        this.asks = asks;
        this.bids = bids;
        this.sequenceNumber = sequenceNumber;
        this.lastChanged = lastChanged;
    }

    //We could look at introducing something like Mapstruct
    //The reason for these mappers is to avoid introducing @JSonIgnore onto the domain model, to make
    //it re-usable in other non-web specific ocntexts
    public static AnonymousOrderBookData from(final OrderBook orderBook) {
        List<MinimumLimitOrderData> mappedAsks = orderBook.getAsks().stream()
                .map(MinimumLimitOrderData::from)
                .toList();
        List<MinimumLimitOrderData> mappedBids = orderBook.getBids().stream()
                .map(MinimumLimitOrderData::from)
                .toList();
        return new AnonymousOrderBookData(mappedAsks, mappedBids, orderBook.getSequenceNumber(), orderBook.getLastChange());
    }

    @JsonAlias("Asks")
    public List<MinimumLimitOrderData> getAsks() {
        return asks;
    }

    @JsonAlias("Bids")
    public List<MinimumLimitOrderData> getBids() {
        return bids;
    }

    @JsonAlias("SequenceNumber")
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @JsonAlias("LastChanged")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getLastChanged() {
        return lastChanged;
    }
}
