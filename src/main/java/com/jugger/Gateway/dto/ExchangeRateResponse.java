package com.jugger.Gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRateResponse {

    private String baseCurrency;
    private String targetCurrency;
    private Double exchangeRate;
    private LocalDateTime timestamp;
}
