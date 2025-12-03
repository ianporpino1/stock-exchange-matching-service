package com.stockexchange.matchingservice.model.dto;


import com.stockexchange.matchingservice.model.OrderType;
import com.stockexchange.matchingservice.model.event.BalanceEvent;

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
    public static CreateOrderCommand from(BalanceEvent.BalanceReserved event) {
        return new CreateOrderCommand(event.orderId(), event.orderId(), event.userId(), event.symbol(), event.price(), event.quantity(), event.orderType(), event.createdAt());
    }
}
