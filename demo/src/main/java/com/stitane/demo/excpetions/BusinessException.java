package com.stitane.demo.excpetions;

public class BusinessException extends RuntimeException {

    /**
     * throw a specific exception
     * @param message a specific message
     */
    public BusinessException(final String message) {
        super(message);
    }

    /**
     * throw a specific exception
     * @param message a specific message
     * @param cause a cause details
     */
    public BusinessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
