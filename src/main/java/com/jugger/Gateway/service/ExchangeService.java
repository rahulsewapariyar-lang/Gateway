package com.jugger.Gateway.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.jugger.Gateway.dto.CurrencyPairResponse;
import com.jugger.Gateway.dto.ExchangeRateRequest;
import com.jugger.Gateway.dto.ExchangeRateResponse;
import com.jugger.Gateway.dto.TransactionRequest;
import com.jugger.Gateway.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class ExchangeService {
    private WebClient webClient;
    public ExchangeService(WebClient webClient) {
        this.webClient = webClient;
    }
    //validate request
    private void validateRequest(ExchangeRateRequest request) {
        if (request.getBaseCurrency() == null || request.getBaseCurrency().trim().isEmpty()) {
            throw new InvalidRequestException("From currency is required");
        }
        if (request.getTargetCurrency() == null || request.getTargetCurrency().trim().isEmpty()) {
            throw new InvalidRequestException("To currency is required");
        }
        if (request.getBaseCurrency().equalsIgnoreCase(request.getTargetCurrency())) {
            throw new InvalidRequestException("From and To currencies cannot be the same");
        }

        if (request.getExchangeRate() == null || request.getExchangeRate() <= 0) {
            throw new InvalidRequestException("Exchange Rate is required and must be greater than 0");
        }

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
    public Mono<ExchangeRateResponse> addRate(ExchangeRateRequest req) {
            validateRequest(req);
            return webClient.post()
                    .uri("/api/v1/exchange-rate/add-rate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(req),ExchangeRateRequest.class)
                    .retrieve()
                    .onStatus(status -> status.value() == 400,
                            res -> res.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(
                                            new InvalidRequestException(
                                                    "Invalid exchange rate data: " + errorBody))))
                    .onStatus(status -> status.value() == 409,
                            res -> Mono.error(
                                    new DuplicateExchangeRateException(
                                            "Exchange rate already exists for " +
                                                    req.getBaseCurrency() + " to " + req.getTargetCurrency())))
                    .onStatus(HttpStatusCode::is5xxServerError,
                    res->
                            Mono.error(new ExternalServiceException( "Unable to fetch exchange rate. Service error.")))
                    .bodyToMono(ExchangeRateResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorMap(TimeoutException.class,
                            ex -> new ExternalServiceException(
                                    "Request timed out. Service took too long to respond"
                            ));
    }

    public Mono<ExchangeRateResponse>updateRate(Long id,ExchangeRateRequest req) {
        validateRequest(req);
        return webClient.patch()
                .uri("/api/v1/exchange-rate/rate/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(req),ExchangeRateRequest.class)
                .retrieve()
                .onStatus(status->status.value() == 404,
                        res->Mono.error(
                                new ExchangeRateNotFound("Exchange rate not found for id: "+id)
                        ))
                .onStatus(status->status.value() == 400,
                        res-> res.bodyToMono(String.class)
                                .flatMap(errorBody->Mono.error(
                                        new InvalidRequestException("Invalid update data: "+ errorBody)
                                )))
                .onStatus(status->status.is5xxServerError(),
                        res->Mono.error(
                                new ExternalServiceException("Unable to update rate. Service error")
                        ))
                .bodyToMono(ExchangeRateResponse.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(TimeoutException.class,
                        ex-> new ExternalServiceException("Request Timed out")
                        );
    }

    public Mono<Void> deleteExchangeRate(Long id) {
        return webClient.delete()
                .uri("/api/v1/exchange-rate/delete/{id}",id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        res -> Mono.error(
                                new ExchangeRateNotFound(
                                        "Cannot delete. Exchange rate not found with ID: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        res -> Mono.error(
                                new ExternalServiceException(
                                        "Unable to delete exchange rate. Service error.")))
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(TimeoutException.class,
                        ex -> new ExternalServiceException("Request timed out"));
    }
}
