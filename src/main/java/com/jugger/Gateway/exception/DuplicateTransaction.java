package com.jugger.Gateway.exception;

public class DuplicateTransaction extends RuntimeException{
    public DuplicateTransaction(String s) {
        super(s);
    }
}
