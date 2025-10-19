package com.stockexchange.matchingservice.controller;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.dto.MatchResponse;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Controller
public class MatchController {
    
    @Autowired
    private MatchingEngine matchingEngine;

    @MutationMapping
    public Mono<MatchResponse> matchOrder(@Argument("order") CreateOrderCommand orderToMatch) {
        return matchingEngine.matchOrder(orderToMatch);
    }



    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        System.out.println(id);
        return matchingEngine.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
