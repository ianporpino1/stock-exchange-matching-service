package com.stockexchange.matchingservice.service;
import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.dto.MatchResponse;
import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MatchingEngine {

    private final AtomicBoolean isReady = new AtomicBoolean(false);

    @Autowired
    private OrderBookService orderBookService;

    private final ConcurrentHashMap<UUID, Order> allOrders = new ConcurrentHashMap<>();

    public MatchResponse matchOrder(CreateOrderCommand command) {
        if (!this.isReady.get()) {
            throw new RuntimeException("Matching Engine está em processo de recuperação.");
        }
        return processInternal(command);
    }

    public Optional<OrderResponse> getOrderById(UUID orderId) {
        Order order = allOrders.get(orderId);
        return Optional.ofNullable(order).map(OrderResponse::new);
    }

    public void startReplay() {
        this.isReady.set(false);
    }

    public void finishReplay() {
        this.isReady.set(true);
    }

    public void replayOrders(List<CreateOrderCommand> commands) {
        startReplay();
        for (CreateOrderCommand command : commands) {
            processInternal(command);
        }
        finishReplay();
    }

    private MatchResponse processInternal(CreateOrderCommand command) {
        Order order = toOrder(command);
        allOrders.put(order.getOrderId(), order);
        return orderBookService.processOrder(order);
    }

    private Order toOrder(CreateOrderCommand command) {
        return new Order(
                command.orderId(),
                command.symbol(),
                command.orderType(),
                command.quantity(),
                command.price(),
                command.userId(),
                command.createdAt()
        );
    }


}
