package com.stockexchange.matchingservice.model.dto;


import com.stockexchange.matchingservice.model.OrderType;

import java.time.Instant;
import java.util.UUID;

public record CreateOrderCommand(UUID commandId,
                                 UUID orderId,
                                 UUID userId,
                                 String symbol,
                                 double price,
                                 int quantity,
                                 OrderType orderType,
                                 Instant createdAt) {
}
