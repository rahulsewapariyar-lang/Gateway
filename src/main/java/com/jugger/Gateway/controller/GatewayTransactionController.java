package com.jugger.Gateway.controller;

import com.jugger.Gateway.dto.CurrencyPairResponse;
import com.jugger.Gateway.dto.TransactionRequest;
import com.jugger.Gateway.dto.TransactionResponse;
import com.jugger.Gateway.service.ExchangeService;
import com.jugger.Gateway.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class GatewayTransactionController {

    private final TransactionService transactionService;
    private final ExchangeService exchangeService;

    public GatewayTransactionController(TransactionService transactionService, ExchangeService exchangeService) {
        this.transactionService = transactionService;
        this.exchangeService = exchangeService;
    }

    @PostMapping
    public Mono<ResponseEntity<TransactionResponse>> createTransaction(@RequestBody TransactionRequest transactionRequest){
        System.out.println("========== CONTROLLER HIT ==========");
        System.out.println("Request: " + transactionRequest);
        return transactionService.createTransaction(transactionRequest)
                .map(ResponseEntity::ok);
    }
    @GetMapping("/exchange-rate/available-pairs")
    public Mono<ResponseEntity<List<CurrencyPairResponse>>> getCurrencyPairs(){
        return exchangeService.getAvailableCurrencyPairs()
                .map(ResponseEntity::ok);
    }
}
