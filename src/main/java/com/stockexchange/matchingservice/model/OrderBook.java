package com.stockexchange.matchingservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderBook {
    private final PriorityBlockingQueue<Order> buyOrders;
    private final PriorityBlockingQueue<Order> sellOrders;

    public OrderBook() {
        this.buyOrders = new PriorityBlockingQueue<>(1, (o1, o2) -> {
            int priceComparison = Double.compare(o2.getPrice(), o1.getPrice());
            if (priceComparison == 0) {
                var t1 = o1.getCreatedAt();
                var t2 = o2.getCreatedAt();
                if (t1 == null && t2 == null) return 0;
                if (t1 == null) return 1;
                if (t2 == null) return -1;
                return t1.compareTo(t2);
            }
            return priceComparison;
        });
        this.sellOrders = new PriorityBlockingQueue<>(1, (o1, o2) -> {
            int priceComparison = Double.compare(o1.getPrice(), o2.getPrice());
            if (priceComparison == 0) {
                var t1 = o1.getCreatedAt();
                var t2 = o2.getCreatedAt();
                if (t1 == null && t2 == null) return 0;
                if (t1 == null) return 1;
                if (t2 == null) return -1;
                return t1.compareTo(t2);
            }
            return priceComparison;
        });
    }

    public synchronized List<Trade> processOrder(Order order) {
        if (order.getType() == OrderType.BUY) {
            buyOrders.add(order);
        } else if (order.getType() == OrderType.SELL) {
            sellOrders.add(order);
        }
        return matchOrders();
    }

    private List<Trade> matchOrders() {
        List<Trade> trades = new ArrayList<>();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                int buyRemainingQuantity = buyOrder.getTotalQuantity() - buyOrder.getExecutedQuantity();
                int sellRemainingQuantity = sellOrder.getTotalQuantity() - sellOrder.getExecutedQuantity();

                int quantity = Math.min(buyRemainingQuantity, sellRemainingQuantity);
                double price = sellOrder.getPrice();

                trades.add(new Trade(UUID.randomUUID(),buyOrder,sellOrder, quantity, price));

                buyOrder.setExecutedQuantity(buyOrder.getExecutedQuantity() + quantity);
                sellOrder.setExecutedQuantity(sellOrder.getExecutedQuantity() + quantity);

                if (buyOrder.getTotalQuantity() == buyOrder.getExecutedQuantity()) {
                    buyOrder.setStatus(OrderStatus.TOTALLY_EXECUTED);
                    buyOrders.poll();
                } else {
                    buyOrder.setStatus(OrderStatus.PARTIALLY_EXECUTED);
                }
                if (sellOrder.getTotalQuantity() == sellOrder.getExecutedQuantity()) {
                    sellOrder.setStatus(OrderStatus.TOTALLY_EXECUTED);
                    sellOrders.poll();
                } else {
                    sellOrder.setStatus(OrderStatus.PARTIALLY_EXECUTED);
                }
            } else {
                break;
            }
        }

        return trades;
    }

    public void addNewOrdersToOrderBook(List<Order> orders) {
        for (Order order : orders) {
            if (!isOrderInBook(order)) {
                if (order.getType() == OrderType.BUY) {
                    buyOrders.add(order);
                } else if (order.getType() == OrderType.SELL) {
                    sellOrders.add(order);
                }
            }

        }
    }
    
    private boolean isOrderInBook(Order order) {
        return buyOrders.stream().anyMatch(o -> o.getOrderId().equals(order.getOrderId())) ||
                sellOrders.stream().anyMatch(o -> o.getOrderId().equals(order.getOrderId()));
    }

    public synchronized List<Order> getBuyOrders() {
        return new ArrayList<>(buyOrders);
    }

    public synchronized List<Order> getSellOrders() {
        return new ArrayList<>(sellOrders);
    }
}
