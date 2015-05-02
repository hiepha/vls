package com.vlls.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hiephn on 2014/07/14.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidItemException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidItemException() {
    }

    public InvalidItemException(String message) {
        super(message);
    }

    public InvalidItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidItemException(Throwable cause) {
        super(cause);
    }

    public InvalidItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
