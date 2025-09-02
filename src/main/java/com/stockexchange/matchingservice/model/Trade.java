package com.stockexchange.matchingservice.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Trade {
    private UUID tradeId;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private UUID buyerUserId;
    private UUID sellerUserId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private Instant executedAt;

    private Order buyOrder;
    private Order sellOrder;

    public Trade(UUID tradeId, Order buyOrder, Order sellOrder, int quantity, BigDecimal price) {
        this.tradeId = tradeId;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.buyOrderId = buyOrder.getOrderId();
        this.sellOrderId = sellOrder.getOrderId();
        this.buyerUserId = buyOrder.getUserId();
        this.sellerUserId = sellOrder.getUserId();
        this.symbol = buyOrder.getSymbol();
        this.quantity = quantity;
        this.price = price;
        this.executedAt = Instant.now();
    }

    public Order getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(Order buyOrder) {
        this.buyOrder = buyOrder;
    }

    public Order getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(Order sellOrder) {
        this.sellOrder = sellOrder;
    }

    public UUID getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(UUID buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public UUID getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(UUID sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public UUID getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(UUID buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public UUID getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(UUID sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

}