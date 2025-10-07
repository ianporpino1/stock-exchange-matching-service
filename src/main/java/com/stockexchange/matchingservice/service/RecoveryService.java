package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class RecoveryService {
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedRestClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient orderServiceClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://order-service")
                .build();
    }

    @Bean
    public CommandLineRunner recoverOrders(WebClient orderServiceClient,
                                           MatchingEngine matchingEngine) {
        return _ -> {
            System.out.println("INICIANDO RECUPERAÇÃO");

            orderServiceClient.get()
                    .uri("/orders/recovery")
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<CreateOrderCommand>() {
                    })
                    .doOnNext(order -> {
                        matchingEngine.replayOrders(List.of(order));
                    })
                    .doOnError(error -> {
                        System.err.println("FALHA DURANTE O STREAM DE RECUPERAÇÃO: " + error.getMessage());
                    })
                    .doOnComplete(() -> {
                        System.out.println("Recuperação concluída.");
                    })
                    .subscribe();
        };
    }
}
