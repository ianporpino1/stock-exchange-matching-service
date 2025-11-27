package com.stockexchange.matchingservice.model.event;

import com.stockexchange.matchingservice.model.dto.TradeResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeExecutedEvent(UUID tradeId,
                                 UUID buyOrderId,
                                 UUID sellOrderId,
                                 UUID buyerUserId,
                                 UUID sellerUserId,
                                 String symbol,
                                 int quantity,
                                 BigDecimal price,
                                 Instant executedAt) {

    public static TradeExecutedEvent from(TradeResponse trade) {
        return new TradeExecutedEvent(trade.tradeId(), trade.buyOrderId(), trade.sellOrderId(), trade.buyerUserId(), trade.sellerUserId(), trade.symbol(), trade.quantity(), trade.price(), trade.executedAt());
    }
}
