package com.jugger.Gateway.controller;

import com.jugger.Gateway.dto.ExchangeRateRequest;
import com.jugger.Gateway.dto.ExchangeRateResponse;
import com.jugger.Gateway.service.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class ExchangeRateManagementController {

    private final ExchangeService exchangeService;

    public ExchangeRateManagementController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/api/v1/exchange-rate/add-rate")
    public Mono<ResponseEntity<ExchangeRateResponse>> addRate(@RequestBody ExchangeRateRequest req){
        return exchangeService.addRate(req)
                .map(res->
                        ResponseEntity.status(HttpStatus.CREATED).body(res));
    }
    @PatchMapping("api/v1/exchange-rate/rate/{id}")
    public Mono<ResponseEntity<ExchangeRateResponse>> updateRate( @PathVariable Long id,@RequestBody ExchangeRateRequest req){
        return exchangeService.updateRate(id,req)
                .map(res->
                        ResponseEntity.status(HttpStatus.OK).body(res));
    }
    @DeleteMapping("/exchange-rate/delete/{id}")
    public Mono<Void> deleteRate(@PathVariable Long id) {
       return exchangeService.deleteExchangeRate(id);
    }
}
