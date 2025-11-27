package com.stockexchange.matchingservice.model.event;



import com.stockexchange.matchingservice.model.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId,
                                UUID userId,
                                String symbol,
                                BigDecimal price,
                                int quantity,
                                OrderType orderType,
                                Instant createdAt) {
}
