package com.jugger.Gateway.repository;

import com.jugger.Gateway.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
    // Check if exact duplicate exists (all fields match)
    boolean existsByFromCurrencyAndToCurrency(
            String fromCurrency,
            String toCurrency,
            Double fromAmount
    );
    // Check if same conversion exists (ignoring description)
    boolean existsByFromCurrencyAndToCurrencyAndFromAmount(
            String fromCurrency,
            String toCurrency,
            Double fromAmount
    );
    // Find duplicate to get details
    Optional<TransactionEntity> findByFromCurrencyAndToCurrencyAndFromAmountAndDescription(
            String fromCurrency,
            String toCurrency,
            Double fromAmount,
            String description
    );
}
