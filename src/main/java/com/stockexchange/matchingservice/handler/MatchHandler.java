package com.stockexchange.matchingservice.handler;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.event.OrderCreatedEvent;
import com.stockexchange.matchingservice.model.event.OrderUpdatedEvent;
import com.stockexchange.matchingservice.model.event.TradeExecutedEvent;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
public class MatchHandler {
    private final MatchingEngine matchingEngine;
    private static final String DESTINATION_HEADER ="spring.cloud.stream.sendto.destination";

    public MatchHandler(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    @Bean
    public Function<Flux<OrderCreatedEvent>, Flux<Message<?>>> handleMatch() {
        return flux -> flux.concatMap(event ->
                matchingEngine.matchOrder(CreateOrderCommand.from(event))
                        .flatMapMany(response -> {

                            List<Message<?>> outputMessages = new ArrayList<>();
                            if (response.orders() != null) {
                                response.orders().forEach(order -> {
                                    var msg = MessageBuilder.withPayload(OrderUpdatedEvent.from(order))
                                            .setHeader(DESTINATION_HEADER, "orders.updated")
                                            .build();
                                    outputMessages.add(msg);
                                });
                            }

                            if (response.trades() != null) {
                                response.trades().forEach(trade -> {
                                    var msg = MessageBuilder.withPayload(TradeExecutedEvent.from(trade))
                                            .setHeader(DESTINATION_HEADER, "trades.executed")
                                            .build();
                                    outputMessages.add(msg);
                                });
                            }

                            return Flux.fromIterable(outputMessages);
                        })
        );
    }
}
