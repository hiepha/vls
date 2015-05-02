package com.vlls.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hiephn on 2014/07/14.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestItemException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidRequestItemException() {
    }

    public InvalidRequestItemException(String message) {
        super(message);
    }

    public InvalidRequestItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestItemException(Throwable cause) {
        super(cause);
    }

    public InvalidRequestItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
