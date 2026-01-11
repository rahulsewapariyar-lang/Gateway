package com.jugger.Gateway.service;

import com.jugger.Gateway.dto.CurrencyPairResponse;
import com.jugger.Gateway.dto.ExchangeRateResponse;
import com.jugger.Gateway.exception.ExchangeRateNotFound;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class ExchangeService {
    private WebClient webClient;
    public ExchangeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ExchangeRateResponse> getRate(String base, String target) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/exchange-rate/targetbyname")
                        .queryParam("baseCurrency", base)
                        .queryParam("targetCurrency", target)
                        .build())
                .retrieve()
                .onStatus(status->status.value() == 404,
                        res->Mono.error(new ExchangeRateNotFound("Exchange rate not available for "+ base + " to " + target)))
                .bodyToMono(ExchangeRateResponse.class) //Tells WebClient: "When the response comes back,// convert the JSON body into a TransactionResponse object" //Mono: Container for exactly ONE response object .onErrorResume(ExchangeRateNotFound.class,error->Mono.error(error)) //TransactionResponse.class=The type to convert JSON into
                .timeout(Duration.ofSeconds(10));
    }
    public Mono<List<CurrencyPairResponse>> getAvailableCurrencyPairs() {  // ← Changed return type
        return webClient.get()
                .uri("/api/v1/exchange-rate/available-pairs")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        res -> Mono.error(new ExchangeRateNotFound("Failed to fetch available currency pairs")))
                .bodyToFlux(CurrencyPairResponse.class)  // ← Matches return type
                .collectList()
                .timeout(Duration.ofSeconds(5));
    }
    public Mono<ExchangeRateResponse> getExchangeRate(String from,String to){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/from/{from}/to/{to}")
                        .build(from,to))
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        res -> {
                            return Mono.error(new ExchangeRateNotFound(
                                    "Exchange rate not found for " + from + " to " + to));
                        })
                .onStatus(status -> status.is5xxServerError(),
                        res -> {
                            return Mono.error(new RuntimeException(
                                    "Exchange Rate Service is unavailable"));
                        })
                .bodyToMono(ExchangeRateResponse.class)
                .timeout(Duration.ofSeconds(5));
    }
}
