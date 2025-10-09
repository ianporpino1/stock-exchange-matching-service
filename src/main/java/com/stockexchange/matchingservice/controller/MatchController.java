package com.stockexchange.matchingservice.controller;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.dto.MatchResponse;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping
public class MatchController {
    
    @Autowired
    private MatchingEngine matchingEngine;
    
    @PostMapping("/match")
    public Mono<ResponseEntity<MatchResponse>> match(@RequestBody Mono<CreateOrderCommand> orderToMatchMono) {
        return orderToMatchMono
                .flatMap(orderToMatch -> matchingEngine.matchOrder(orderToMatch))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()));
    }


    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        System.out.println(id);
        return matchingEngine.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
