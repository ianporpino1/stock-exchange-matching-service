package com.stockexchange.matchingservice.controller.dto;

import java.util.List;

public record MatchResponse(List<OrderResponse> orders) {
}
