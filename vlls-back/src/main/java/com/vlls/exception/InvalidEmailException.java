package com.vlls.exception;

/**
 * Created by hiephn on 2014/07/14.
 */
public class InvalidEmailException extends InvalidRequestItemException {
    private static final long serialVersionUID = 1L;
    public InvalidEmailException() {
    }

    public InvalidEmailException(String message) {
        super(message);
    }

    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEmailException(Throwable cause) {
        super(cause);
    }

    public InvalidEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
