package com.stockexchange.matchingservice.model;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;


public class OrderBook {
    private final NavigableMap<BigDecimal, Queue<Order>> buyOrders = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    private final NavigableMap<BigDecimal, Queue<Order>> sellOrders = new ConcurrentSkipListMap<>();

    public List<Trade> processOrder(Order order) {
        if (order.getType() == OrderType.BUY) {
            buyOrders.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
        } else if (order.getType() == OrderType.SELL) {
            sellOrders.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
        }
        return matchOrders();
    }

    private List<Trade> matchOrders() {
        List<Trade> trades = new ArrayList<>();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Map.Entry<BigDecimal, Queue<Order>> bestBuyEntry = buyOrders.firstEntry();
            Map.Entry<BigDecimal, Queue<Order>> bestSellEntry = sellOrders.firstEntry();

            if (bestBuyEntry == null || bestSellEntry == null) {
                continue;
            }

            Order buyOrder = bestBuyEntry.getValue().peek();
            Order sellOrder = bestSellEntry.getValue().peek();

            if (buyOrder == null) {
                buyOrders.remove(bestBuyEntry.getKey(), bestBuyEntry.getValue());
                continue;
            }
            if (sellOrder == null) {
                sellOrders.remove(bestSellEntry.getKey(), bestSellEntry.getValue());
                continue;
            }

            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                BigDecimal tradePrice = buyOrder.getCreatedAt().isBefore(sellOrder.getCreatedAt())
                        ? buyOrder.getPrice()
                        : sellOrder.getPrice();

                int tradeQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());

                trades.add(new Trade(UUID.randomUUID(), buyOrder, sellOrder, tradeQuantity, tradePrice));

                updateOrder(buyOrder, tradeQuantity);
                updateOrder(sellOrder, tradeQuantity);

                if (buyOrder.getRemainingQuantity() == 0) {
                    bestBuyEntry.getValue().poll();
                }
                if (sellOrder.getRemainingQuantity() == 0) {
                    bestSellEntry.getValue().poll();
                }

                if (bestBuyEntry.getValue().isEmpty()) {
                    buyOrders.remove(bestBuyEntry.getKey());
                }
                if (bestSellEntry.getValue().isEmpty()) {
                    sellOrders.remove(bestSellEntry.getKey());
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
}
