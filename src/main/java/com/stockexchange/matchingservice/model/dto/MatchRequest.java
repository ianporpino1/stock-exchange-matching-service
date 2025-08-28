package com.stockexchange.matchingservice.model.dto;

import com.stockexchange.matchingservice.model.Order;

public record MatchRequest(Order order) {
}
