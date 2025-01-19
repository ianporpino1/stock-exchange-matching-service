package com.stockexchange.matchingservice.controller;

import com.stockexchange.matchingservice.controller.dto.MatchRequest;
import com.stockexchange.matchingservice.controller.dto.OrderResponse;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/match")
public class MatchController {
    
    @Autowired
    private MatchingEngine matchingEngine;
    
    @PostMapping
    public ResponseEntity<OrderResponse> match(@RequestBody MatchRequest orderToMatch) {
        //1. salvar no banco de dados
        //2. recuperar ordens de mesmo simbolo p montar orderbook
        //3. tentar fazer o match
        //4. se houve match, altera status das ordens no banco

        System.out.println(orderToMatch);
        OrderResponse orderResponse = matchingEngine.matchOrder(orderToMatch);
        return ResponseEntity.ok(orderResponse);
    }
}
