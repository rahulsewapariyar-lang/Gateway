package com.jugger.Gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String fromCurrency;
    private Double fromAmount;
    private String toCurrency;
    private Double toAmount;
    private Double exchangeRate;
    private String description;
    private LocalDateTime createdAt;
}