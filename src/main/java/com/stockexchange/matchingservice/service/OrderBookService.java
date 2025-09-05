package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.model.dto.MatchResponse;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import com.stockexchange.matchingservice.model.Trade;
import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.OrderBook;
import com.stockexchange.matchingservice.model.dto.TradeResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderBookService {
    private final ConcurrentHashMap<String, OrderBook> orderBooks;

    public OrderBookService() {
        this.orderBooks = new ConcurrentHashMap<>();
    }

    OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, k -> new OrderBook());
    }

    public MatchResponse processOrder(Order order) {
        OrderBook orderBook = getOrderBook(order.getSymbol());

        List<Trade> trades = orderBook.processOrder(order);

        List<OrderResponse> orderResponses = new ArrayList<>();
        if (trades.isEmpty()) {
            orderResponses.add(new OrderResponse(order));
            return new MatchResponse(orderResponses);
        }
        Set<Order> impactedOrders = new HashSet<>();
        impactedOrders.add(order);
        trades.forEach(trade -> {
            if (trade.getBuyOrder() != null) impactedOrders.add(trade.getBuyOrder());
            if (trade.getSellOrder() != null) impactedOrders.add(trade.getSellOrder());
        });
        impactedOrders.forEach(o -> orderResponses.add(new OrderResponse(o)));
        List<TradeResponse> tradeResponses = trades.stream()
                .map(TradeResponse::new)
                .collect(Collectors.toList());
        return new MatchResponse(orderResponses, tradeResponses);
    }

}
