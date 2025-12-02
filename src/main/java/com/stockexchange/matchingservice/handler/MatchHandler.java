package com.stockexchange.matchingservice.handler;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import com.stockexchange.matchingservice.model.dto.TradeResponse;
import com.stockexchange.matchingservice.model.event.OrderCreatedEvent;
import com.stockexchange.matchingservice.model.event.OrderUpdatedEvent;
import com.stockexchange.matchingservice.model.event.TradeExecutedEvent;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.function.Function;

@Configuration
public class MatchHandler {
    private final MatchingEngine matchingEngine;

    public MatchHandler(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    @Bean
    public Function<Flux<Message<OrderCreatedEvent>>, Flux<Message<?>>> handleMatch() {
        return flux -> flux
                .filter(msg -> "order.created".equals(msg.getHeaders().get("eventType")))
                .map(Message::getPayload)
                .concatMap(event ->
                matchingEngine.matchOrder(CreateOrderCommand.from(event))
                        .flatMapMany(response ->
                                Flux.merge(
                                        publishOrders(response.orders()),
                                        publishTrades(response.trades())
                                )
                        )
        );
    }

    private Flux<Message<?>> publishOrders(List<OrderResponse> orders) {
        if (orders == null || orders.isEmpty()) {
            return Flux.empty();
        }
        return Flux.fromIterable(orders)
                .map(order ->
                        MessageBuilder.withPayload(OrderUpdatedEvent.from(order))
                                .setHeader("spring.cloud.stream.sendto.destination", "order.events")
                                .setHeader("eventType", "order.updated")
                                .build()
                );
    }

    private Flux<Message<?>> publishTrades(List<TradeResponse> trades) {
        if (trades == null || trades.isEmpty()) {
            return Flux.empty();
        }
        return Flux.fromIterable(trades)
                .map(trade ->
                        MessageBuilder.withPayload(TradeExecutedEvent.from(trade))
                                .setHeader("spring.cloud.stream.sendto.destination", "trade.events")
                                .setHeader("eventType", "trade.executed")
                                .build()
                );
    }
}
