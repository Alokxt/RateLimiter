package com.example.rateLimiter.exceptions;

public class InvalidApiKeyException extends RuntimeException{
    public InvalidApiKeyException(String message){
        super(message);
    }
}
