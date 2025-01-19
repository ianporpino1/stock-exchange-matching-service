package com.stockexchange.matchingservice.controller.dto;

import java.util.UUID;

public record MatchRequest(OrderRequest orderRequest, UUID userId) {
}
