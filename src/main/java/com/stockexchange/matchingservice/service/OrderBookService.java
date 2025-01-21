package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.controller.dto.MatchResponse;
import com.stockexchange.matchingservice.controller.dto.OrderResponse;
import com.stockexchange.matchingservice.model.Execution;
import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.OrderBook;
import com.stockexchange.matchingservice.model.OrderStatus;
import com.stockexchange.matchingservice.repository.OrderBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderBookService {
    private final ConcurrentHashMap<String, OrderBook> orderBooks;

    @Autowired
    private OrderBookRepository orderBookRepository;


    public OrderBookService() {
        this.orderBooks = new ConcurrentHashMap<>();
    }

    private OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, k -> new OrderBook());
    }

    public MatchResponse processOrder(Order order) {
        OrderBook orderBook = getOrderBook(order.getSymbol());

        orderBookRepository.save(order);
        
        List<Order> openOrders = orderBookRepository.findOpenOrders(
                order.getSymbol(),
                OrderStatus.ACCEPTED,
                OrderStatus.PARTIALLY_EXECUTED);
        orderBook.addNewOrdersToOrderBook(openOrders);
        List<Execution> executions = orderBook.processOrder(order);

        List<OrderResponse> orderResponses = new ArrayList<>();
        if(executions.isEmpty()) {
            orderResponses.add(new OrderResponse(order));
            return new MatchResponse(orderResponses);
        }
        
        //executionService.saveAllExecutions(executions);
        Set<Order> ordersToSave = new HashSet<>();
        ordersToSave.add(order);
        executions.forEach(execution -> {
            if (execution.getBuyOrder() != null) {
                ordersToSave.add(execution.getBuyOrder());
            }
            if (execution.getSellOrder() != null) {
                ordersToSave.add(execution.getSellOrder());
            }
        });
        orderBookRepository.saveAll(ordersToSave);

        ordersToSave.forEach(o -> {
            int executedQuantity = executions.stream()
                    .filter(ex -> ex.getBuyOrder().equals(o) || ex.getSellOrder().equals(o))
                    .mapToInt(Execution::getQuantity)
                    .sum();
            orderResponses.add(new OrderResponse(o));
        });
        return new MatchResponse(orderResponses);
    }

}
