package com.project.Ecommerce.Exceptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {

      super(message);
    }
}
