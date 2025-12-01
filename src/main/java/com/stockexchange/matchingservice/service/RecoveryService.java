package com.stockexchange.matchingservice.service;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
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

//    @Bean
//    public CommandLineRunner recoverOrders(WebClient orderServiceClient,
//                                           MatchingEngine matchingEngine) {
//
//        return _ -> {
//            System.out.println("INICIANDO RECUPERAÇÃO");
//            //TALVEZ MAIS TARDE ADICIONAR RETRY
////            try {
//            List<CreateOrderCommand> orders = orderServiceClient.get()
//                    .uri("/orders/recovery")
//                    .retrieve()
//                    .bodyToFlux(new ParameterizedTypeReference<CreateOrderCommand>() {})
//                    .collectList()
//                    .block();
//
//                if (orders != null) {
//                    System.out.println("Recuperando " + orders.size() + " ordens");
//                    matchingEngine.replayOrders(orders);
//                }
//                System.out.println("Recuperação concluída");
////            } catch (Exception e) {
////                System.err.println("ORDER SERVICE NAO DISPONIVEL: " + e.getMessage());
////            }
//
//        };
//    }
}
