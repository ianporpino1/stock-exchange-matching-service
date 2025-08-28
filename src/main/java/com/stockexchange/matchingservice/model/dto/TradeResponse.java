package com.stockexchange.matchingservice.model.dto;

import com.stockexchange.matchingservice.model.Trade;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(UUID tradeId,
        UUID buyOrderId,
        UUID sellOrderId,
         UUID buyerUserId,
         UUID sellerUserId,
         String symbol,
         int quantity,
         BigDecimal price,
         Instant executedAt
) {
    public TradeResponse(Trade trade) {
        this(
                trade.getTradeId(),
                trade.getBuyOrder().getOrderId(),
                trade.getSellOrder().getOrderId(),
                trade.getBuyOrder().getUserId(),
                trade.getSellOrder().getUserId(),
                trade.getSymbol(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getExecutedAt()
        );
    }
}
