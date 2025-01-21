package com.stockexchange.matchingservice.controller.dto;

import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.OrderStatus;
import com.stockexchange.matchingservice.model.OrderType;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        OrderStatus orderStatus,
        OrderType orderType,
        String symbol,
        double price,
        int executedQuantity,
        int totalQuantity,
        Instant orderDate,
        UUID userId) {

    public OrderResponse(Order order) {
        this(order.getId(),
                order.getStatus(),
                order.getType(),
                order.getSymbol(),
                order.getPrice(),
                order.getExecutedQuantity(),
                order.getTotalQuantity(),
                order.getCreatedAt(),
                order.getUserId());
    }
}
