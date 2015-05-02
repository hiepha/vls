package com.vlls.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hiephn on 2014/07/14.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoInstanceException extends Exception {
    private static final long serialVersionUID = 1L;
    public NoInstanceException() {
    }

    public NoInstanceException(String message) {
        super(message);
    }

    public NoInstanceException(String type, Object key) {
        super(String.format("%s not found: %s", type, key));
    }

    public NoInstanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInstanceException(Throwable cause) {
        super(cause);
    }

    public NoInstanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
