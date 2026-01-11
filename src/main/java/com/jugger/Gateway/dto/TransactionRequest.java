package com.jugger.Gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionRequest {

    private String fromCurrency;
    private String toCurrency;
    private Double fromAmount;
    private Double toAmount;
    private String description;
    private LocalDateTime timestamp;
}
