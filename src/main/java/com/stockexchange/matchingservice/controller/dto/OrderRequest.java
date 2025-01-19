package com.stockexchange.matchingservice.controller.dto;


import com.stockexchange.matchingservice.model.OrderType;

public record OrderRequest(String symbol, double price, int quantity, OrderType orderType) {
}
