package com.stockexchange.matchingservice.handler;

import com.stockexchange.matchingservice.model.dto.CreateOrderCommand;
import com.stockexchange.matchingservice.model.dto.OrderResponse;
import com.stockexchange.matchingservice.model.dto.TradeResponse;
import com.stockexchange.matchingservice.model.event.BalanceEvent;
import com.stockexchange.matchingservice.model.event.OrderEvent;
import com.stockexchange.matchingservice.model.event.OrderUpdatedEvent;
import com.stockexchange.matchingservice.model.event.TradeExecutedEvent;
import com.stockexchange.matchingservice.service.MatchingEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;

@Configuration
public class MatchHandler {
    private final MatchingEngine matchingEngine;

    public MatchHandler(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    @Bean
    public Function<Flux<Message<BalanceEvent.BalanceReserved>>, Flux<Message<?>>> handleMatch(
            @Value("${market.open:10:00}") LocalTime marketOpen,
            @Value("${market.close:17:00}") LocalTime marketClose
    ) {
        return flux -> flux
                .filter(msg -> "balance.reserved".equals(msg.getHeaders().get("eventType")))
                .concatMap(message ->
                        validateMarketHours(message,marketOpen,marketClose)
                                .switchIfEmpty(
                                        matchingEngine.matchOrder(CreateOrderCommand.from(message.getPayload()))
                                                .flatMapMany(response -> Flux.merge(
                                                        publishOrders(response.orders()),
                                                        publishTrades(response.trades())
                                                ))
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

    private Flux<Message<?>> validateMarketHours(Message<BalanceEvent.BalanceReserved> message, LocalTime marketOpen, LocalTime marketClose) {
        BalanceEvent.BalanceReserved event = message.getPayload();
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalTime now = LocalTime.now(zone);

        boolean isMarketClosed = now.isBefore(marketOpen) || now.isAfter(marketClose);

        if (isMarketClosed) {
            var rejection = new OrderEvent.OrderRejected(event.orderId(),event.userId(),event.price(),event.quantity(),event.orderType());
            return Flux.just(
                    MessageBuilder.withPayload(rejection)
                            .setHeader("spring.cloud.stream.sendto.destination", "order.events")
                            .setHeader("eventType", "order.rejected")
                            .build()
            );
        }
        return Flux.empty();
    }
}
