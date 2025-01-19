package com.stockexchange.matchingservice.controller;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.stockexchange.matchingservice.controller.dto.MatchRequest;
import com.stockexchange.matchingservice.controller.dto.OrderResponse;
import com.stockexchange.matchingservice.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/match")
public class MatchController {
    
    @PostMapping
    public ResponseEntity<OrderResponse> match(@RequestBody MatchRequest orderToMatch) {
        System.out.println(orderToMatch);
        return new ResponseEntity<>(new OrderResponse(
                UUID.randomUUID(), 
                OrderStatus.ACCEPTED, 
                0,
                orderToMatch.orderRequest().quantity(),
                Instant.now(),
                orderToMatch.userId()), HttpStatus.OK);
    }
}
