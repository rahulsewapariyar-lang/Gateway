package com.jugger.Gateway.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="transaction_data")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    private String fromCurrency;
    private String toCurrency;
    private Double fromAmount;
    private Double toAmount;
    private Double exchangeRate;
    private String description;
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate(){
        timestamp = LocalDateTime.now();
    }

}
