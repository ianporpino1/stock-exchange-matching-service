package com.stockexchange.matchingservice.service;
import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.dto.MatchResponse;
import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchingEngine {
    
    @Autowired
    private OrderBookService orderBookService;

    private final ConcurrentHashMap<UUID, Order> allOrders = new ConcurrentHashMap<>();

    public MatchResponse matchOrder(CreateOrderCommand command) {
        Order order = new Order(command.orderId(),
                command.symbol(),
                command.orderType(),
                command.quantity(),
                command.price(),
                command.userId(),
                command.createdAt());
        allOrders.put(order.getOrderId(), order);
        return orderBookService.processOrder(order);
    }

    public Optional<OrderResponse> getOrderById(UUID orderId) {
        Order order = allOrders.get(orderId);
        return Optional.ofNullable(order).map(OrderResponse::new);
    }
}
