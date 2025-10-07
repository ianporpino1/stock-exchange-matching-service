package com.stockexchange.matchingservice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderBook {
    private final PriorityBlockingQueue<Order> buyOrders;
    private final PriorityBlockingQueue<Order> sellOrders;

    private static final Comparator<Order> BUY_ORDER_COMPARATOR =
            Comparator.comparing(Order::getPrice).reversed()
                    .thenComparing(
                            Order::getCreatedAt,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    );

    private static final Comparator<Order> SELL_ORDER_COMPARATOR =
            Comparator.comparing(Order::getPrice)
                    .thenComparing(
                            Order::getCreatedAt,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    );

    public OrderBook() {
        this.buyOrders = new PriorityBlockingQueue<>(100, BUY_ORDER_COMPARATOR);
        this.sellOrders = new PriorityBlockingQueue<>(100, SELL_ORDER_COMPARATOR);
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

            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {

                BigDecimal tradePrice = buyOrder.getCreatedAt().isBefore(sellOrder.getCreatedAt())
                        ? buyOrder.getPrice()
                        : sellOrder.getPrice();

                int tradeQuantity  = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());

                trades.add(new Trade(UUID.randomUUID(),buyOrder,sellOrder, tradeQuantity , tradePrice));

                updateOrder(buyOrder, tradeQuantity);
                updateOrder(sellOrder, tradeQuantity);

                if (buyOrder.getRemainingQuantity() == 0) {
                    buyOrders.poll();
                }
                if (sellOrder.getRemainingQuantity() == 0) {
                    sellOrders.poll();
                }
            } else {
                break;
            }
        }

        return trades;
    }

    private void updateOrder(Order order, int executedQuantity) {
        order.setExecutedQuantity(order.getExecutedQuantity() + executedQuantity);
        if (order.getRemainingQuantity() == 0) {
            order.setStatus(OrderStatus.TOTALLY_EXECUTED);
        } else {
            order.setStatus(OrderStatus.PARTIALLY_EXECUTED);
        }
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
