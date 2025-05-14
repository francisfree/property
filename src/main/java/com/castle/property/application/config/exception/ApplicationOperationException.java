package com.castle.property.application.config.exception;




import com.castle.property.application.config.exception.handler.Translator;

import java.util.List;

/**
 * Custom Exception Class
 *
 * @author franc
 */
public class ApplicationOperationException extends IllegalArgumentException {

    private List<String> errors;

    public ApplicationOperationException() {
       
    }

    public ApplicationOperationException(String s, Object... args) {
        super(Translator.toLocale(s, args));
    }

    /**
     *
     * @param message Error message
     */
    public ApplicationOperationException(String message) {
        super(Translator.toLocale(message));
    }

    /**
     *
     * @param cause {@link Throwable}
     */
    public ApplicationOperationException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param message Error message
     * @param cause {@link Throwable}
     */
    public ApplicationOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationOperationException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
