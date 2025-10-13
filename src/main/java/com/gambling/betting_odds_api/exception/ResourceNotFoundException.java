package com.gambling.betting_odds_api.exception;

// Custom exception for when a resource (e.g., odds) is not found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id){
        super(String.format("%s not found with id: %d", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue){
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }

}
