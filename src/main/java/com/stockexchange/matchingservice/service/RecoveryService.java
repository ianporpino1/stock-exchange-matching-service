package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class RecoveryService {
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient orderServiceClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://order-service")
                .build();
    }

    @Bean
    public CommandLineRunner recoverOrders(RestClient orderServiceClient,
                                           MatchingEngine matchingEngine) {

        return _ -> {
            System.out.println("INICIANDO RECUPERAÇÃO");
            List<CreateOrderCommand> orders = orderServiceClient.get()
                    .uri("/orders/recovery")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (orders != null) {
                matchingEngine.replayOrders(orders);
            }
            System.out.println("Recuperação concluída");
        };
    }
}
