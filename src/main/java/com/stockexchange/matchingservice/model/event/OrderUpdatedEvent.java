package com.stockexchange.matchingservice.model.event;



import com.stockexchange.matchingservice.model.OrderStatus;
import com.stockexchange.matchingservice.model.OrderType;
import com.stockexchange.matchingservice.model.dto.OrderResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderUpdatedEvent(UUID orderId,
                                OrderStatus orderStatus,
                                OrderType orderType,
                                String symbol,
                                BigDecimal price,
                                int executedQuantity,
                                int totalQuantity,
                                Instant orderDate,
                                UUID userId) {
    public static OrderUpdatedEvent from(OrderResponse order) {
        return new OrderUpdatedEvent(order.orderId(), order.orderStatus(), order.orderType(), order.symbol(), order.price(), order.executedQuantity(), order.totalQuantity(), order.orderDate(), order.userId());
    }
}
