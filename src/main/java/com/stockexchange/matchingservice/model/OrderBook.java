package com.stockexchange.matchingservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderBook {
    private final PriorityBlockingQueue<Order> buyOrders;
    private final PriorityBlockingQueue<Order> sellOrders;

    public OrderBook() {
        this.buyOrders = new PriorityBlockingQueue<>(1, (o1, o2) -> {
            int priceComparison = Double.compare(o2.getPrice(), o1.getPrice());
            if (priceComparison == 0) {
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }
            return priceComparison;
        });
        this.sellOrders = new PriorityBlockingQueue<>(1, (o1, o2) -> {
            int priceComparison = Double.compare(o1.getPrice(), o2.getPrice());
            if (priceComparison == 0) {
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }
            return priceComparison;
        });
    }

    public synchronized List<Execution> processOrder(Order order) {
        if (order.getType() == OrderType.BUY) {
            buyOrders.add(order);
        } else if (order.getType() == OrderType.SELL) {
            sellOrders.add(order);
        }
        return matchOrders();
    }

    private List<Execution> matchOrders() {
        List<Execution> executions = new ArrayList<>();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                int buyRemainingQuantity = buyOrder.getTotalQuantity() - buyOrder.getExecutedQuantity();
                int sellRemainingQuantity = sellOrder.getTotalQuantity() - sellOrder.getExecutedQuantity();

                int quantity = Math.min(buyRemainingQuantity, sellRemainingQuantity);
                double price = sellOrder.getPrice();

                executions.add(new Execution(buyOrder, sellOrder, quantity, price));

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

        return executions;
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
        return buyOrders.stream().anyMatch(o -> o.getId().equals(order.getId())) ||
                sellOrders.stream().anyMatch(o -> o.getId().equals(order.getId()));
    }

    public synchronized List<Order> getBuyOrders() {
        return new ArrayList<>(buyOrders);
    }

    public synchronized List<Order> getSellOrders() {
        return new ArrayList<>(sellOrders);
    }
}
