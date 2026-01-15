package com.jugger.Gateway.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateRequest {

    @NotNull
    @Size(min = 3, max = 3)
    private String baseCurrency;
    @Size(min = 3, max = 3)
    private String targetCurrency;

    @NotNull
    @PositiveOrZero(message = "Cannot be negative")
    private Double exchangeRate;
}
