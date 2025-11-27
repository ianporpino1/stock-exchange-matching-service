package com.stockexchange.matchingservice.controller;


import com.stockexchange.matchingservice.model.dto.OrderResponse;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping
public class MatchController {
    
    @Autowired
    private MatchingEngine matchingEngine;


    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        System.out.println(id);
        return matchingEngine.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
