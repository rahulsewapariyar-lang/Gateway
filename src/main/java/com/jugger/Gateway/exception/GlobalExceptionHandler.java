package com.jugger.Gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> invalidRequestException(InvalidRequestException cex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(404)
                .message(cex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = ExchangeRateNotFound.class)
    public Mono<ResponseEntity<ErrorResponse>> exchangeRateNotFound(ExchangeRateNotFound cex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(404)
                .message(cex.getMessage())
                .build();
        return
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }
    @ExceptionHandler(DuplicateTransaction.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDuplicateTransaction(
            DuplicateTransaction ex,ServerWebExchange exchange) {

        String errorMessage = ex.getMessage() != null
                ? ex.getMessage()
                : "Duplicate transaction found";
        System.out.println(errorMessage);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }
}
