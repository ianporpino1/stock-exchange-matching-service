package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.controller.dto.MatchRequest;
import com.stockexchange.matchingservice.controller.dto.OrderRequest;
import com.stockexchange.matchingservice.controller.dto.OrderResponse;
import com.stockexchange.matchingservice.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MatchingEngine {
    
    @Autowired
    private OrderBookService orderBookService;

    public OrderResponse matchOrder(MatchRequest matchRequest) {
        OrderRequest orderRequest = matchRequest.orderRequest();
        Order order = new Order(
                orderRequest.symbol(),
                orderRequest.orderType(),
                orderRequest.quantity(),
                orderRequest.price(),
                matchRequest.userId());
        return orderBookService.processOrder(order);
    }
}
