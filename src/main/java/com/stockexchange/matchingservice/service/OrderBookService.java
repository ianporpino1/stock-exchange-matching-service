package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.controller.dto.OrderResponse;
import com.stockexchange.matchingservice.model.Execution;
import com.stockexchange.matchingservice.model.Order;
import com.stockexchange.matchingservice.model.OrderBook;
import com.stockexchange.matchingservice.model.OrderStatus;
import com.stockexchange.matchingservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderBookService {
    private final ConcurrentHashMap<String, OrderBook> orderBooks;

    @Autowired
    private OrderRepository orderRepository;//orderBookRepository
//    @Autowired
//    private ExecutionService executionService;

    public OrderBookService() {
        this.orderBooks = new ConcurrentHashMap<>();
    }

    private OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, k -> new OrderBook());
    }

    public OrderResponse processOrder(Order order) {
        OrderBook orderBook = getOrderBook(order.getSymbol());
        
        orderRepository.save(order);
        
        List<Order> openOrders = orderRepository.findOpenOrders(
                order.getSymbol(),
                OrderStatus.ACCEPTED,
                OrderStatus.PARTIALLY_EXECUTED);
        orderBook.addNewOrdersToOrderBook(openOrders);
        List<Execution> executions = orderBook.processOrder(order);
        
        if(executions.isEmpty()) {
            //orderService.saveOrder(order);
            return new OrderResponse(
                    order.getId(),
                    order.getStatus(),
                    order.getExecutedQuantity(),
                    order.getTotalQuantity(),
                    order.getCreatedAt(),
                    order.getUserId()
            );
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
        //orderService.saveAllOrders(ordersToSave);

        int executedQuantity = executions.stream()
                .filter(ex -> ex.getBuyOrder().equals(order) || ex.getSellOrder().equals(order))
                .mapToInt(Execution::getQuantity)
                .sum();
        
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                executedQuantity,
                order.getTotalQuantity(),
                order.getCreatedAt(),
                order.getUserId()
        );
    }

}
