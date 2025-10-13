package com.gambling.betting_odds_api.exception;

// Custom exception for odds validation failures
public class InvalidOddsException extends RuntimeException {

    public InvalidOddsException(String message){
        super(message);
    }

}
