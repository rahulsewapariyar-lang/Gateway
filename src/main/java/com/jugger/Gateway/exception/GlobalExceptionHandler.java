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

    //400
    @ExceptionHandler(value = InvalidRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> invalidRequestException(InvalidRequestException cex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(cex.getMessage())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }
    //404
    @ExceptionHandler(value = ExchangeRateNotFound.class)
    public Mono<ResponseEntity<ErrorResponse>> exchangeRateNotFound(ExchangeRateNotFound cex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(cex.getMessage())
                .build();
        return
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }
    //409
    @ExceptionHandler(DuplicateExchangeRateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDuplicate(DuplicateExchangeRateException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
    }
    //503 (500)
    @ExceptionHandler(ExternalServiceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleExternalServiceException(ExternalServiceException ex,ServerWebExchange exchange) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message(ex.getMessage())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    // 409 - Conflict (Duplicate)
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
    // 500 - Internal Server Error (Fallback for unexpected errors)
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}
