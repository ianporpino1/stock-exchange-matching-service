package com.stockexchange.matchingservice.model.dto;

import com.stockexchange.matchingservice.model.Trade;

import java.util.List;

public record MatchResponse(List<OrderResponse> orders, List<TradeResponse> trades) {
    public MatchResponse(List<OrderResponse> orders) {
        this(orders, List.of());
    }
}
