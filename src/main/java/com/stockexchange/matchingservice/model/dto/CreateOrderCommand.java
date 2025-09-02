package com.stockexchange.matchingservice.model.dto;


import com.stockexchange.matchingservice.model.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateOrderCommand(UUID commandId,
                                 UUID orderId,
                                 UUID userId,
                                 String symbol,
                                 BigDecimal price,
                                 int quantity,
                                 OrderType orderType,
                                 Instant createdAt) {
}
