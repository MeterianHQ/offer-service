package com.ovoenergy.offer.exception;

public class InternalBaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String errorMessageProperty;

    public InternalBaseException(String message) {
        this.errorMessageProperty = message;
    }

    public String getErrorMessageProperty() {
        return errorMessageProperty;
    }
}
