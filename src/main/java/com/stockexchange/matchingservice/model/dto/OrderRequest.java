package com.stockexchange.matchingservice.model.dto;


import com.stockexchange.matchingservice.model.OrderType;

public record OrderRequest(String symbol, double price, int quantity, OrderType orderType) {
}
