package com.stockexchange.matchingservice.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String symbol;
    private OrderType type;
    private OrderStatus status;
    private double price;
    private int executedQuantity;
    private int totalQuantity;
    private Instant createdAt;
    
    private UUID userId;

    public Order(String symbol, OrderType type, int quantity, double price, UUID userId) {
        this.symbol = symbol;
        this.type = type;
        this.price = price;
        this.userId = userId;
        this.executedQuantity = 0;
        this.totalQuantity = quantity;
        this.createdAt = Instant.now();
        this.status = OrderStatus.ACCEPTED;
    }

    public Order() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(int executedQuantity) {
        this.executedQuantity = executedQuantity;
    }
}
