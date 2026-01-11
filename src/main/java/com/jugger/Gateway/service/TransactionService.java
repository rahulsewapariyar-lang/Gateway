package com.jugger.Gateway.service;

import com.jugger.Gateway.dto.TransactionRequest;
import com.jugger.Gateway.dto.ExchangeRateResponse;
import com.jugger.Gateway.dto.TransactionResponse;
import com.jugger.Gateway.exception.InvalidRequestException;
import com.jugger.Gateway.model.TransactionEntity;
import com.jugger.Gateway.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private ExchangeService exchangeService;
    public  TransactionService(TransactionRepository transactionRepository,ExchangeService exchangeService){
        this.transactionRepository=transactionRepository;
        this.exchangeService=exchangeService;
    }

    //create transaction with currency conversion
    public Mono<TransactionResponse> createTransaction(TransactionRequest transactionRequest){
        validateRequest(transactionRequest);

        return exchangeService.getRate(transactionRequest.getFromCurrency(),transactionRequest.getToCurrency())
                .map(exchangeRate-> {
                    if (exchangeRate == null || exchangeRate.getExchangeRate() == null){
                        throw new InvalidRequestException("Invalid currency pair or invalid response from Finance Currency");
                    }
                    //this exchange rate is the transaction response dto from project A
                    TransactionEntity transactionEntity = new TransactionEntity();

                    transactionEntity.setFromCurrency(transactionRequest.getFromCurrency());
                    transactionEntity.setToCurrency(transactionRequest.getToCurrency());
                    transactionEntity.setExchangeRate(exchangeRate.getExchangeRate());
                    transactionEntity.setDescription(transactionRequest.getDescription());
                    transactionEntity.setFromAmount(transactionRequest.getFromAmount());
                    transactionEntity.setToAmount(transactionRequest.getFromAmount() * exchangeRate.getExchangeRate());
                    return transactionRepository.save(transactionEntity);
                })
                .map(this::convertToResponse);
    }
    // Validate transaction request
    private void validateRequest(TransactionRequest request) {
        if (request.getFromCurrency() == null || request.getFromCurrency().trim().isEmpty()) {
            throw new InvalidRequestException("From currency is required");
        }
        if (request.getToCurrency() == null || request.getToCurrency().trim().isEmpty()) {
            throw new InvalidRequestException("To currency is required");
        }

        if (request.getFromAmount() == null || request.getFromAmount() <= 0) {
            throw new InvalidRequestException("Amount must be greater than 0");
        }
        if (request.getFromCurrency().equalsIgnoreCase(request.getToCurrency())) {
            throw new InvalidRequestException("From and To currencies cannot be the same");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new InvalidRequestException("Description is required");
        }

    }
    // GET all transactions
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse convertToResponse(TransactionEntity transactionEntity){
        return TransactionResponse
                .builder()
                .id(transactionEntity.getId())
                .fromCurrency(transactionEntity.getFromCurrency())
                .toCurrency(transactionEntity.getToCurrency())
                .description(transactionEntity.getDescription())
                .exchangeRate(transactionEntity.getExchangeRate())
                .fromAmount(transactionEntity.getFromAmount())
                .toAmount(transactionEntity.getToAmount())
                .createdAt(transactionEntity.getTimestamp())
                .build();
    }
}
