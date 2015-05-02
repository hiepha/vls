package com.vlls.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hiephn on 2014/07/14.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedItemException extends Exception {
    private static final long serialVersionUID = 1L;
    public DuplicatedItemException() {
    }

    public DuplicatedItemException(String message) {
        super(message);
    }

    public DuplicatedItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedItemException(Throwable cause) {
        super(cause);
    }

    public DuplicatedItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
