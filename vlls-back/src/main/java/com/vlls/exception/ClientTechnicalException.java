package com.vlls.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hiephn on 2014/07/12.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientTechnicalException extends Exception {
    private static final long serialVersionUID = 1L;
    public ClientTechnicalException() {
        super();
    }

    public ClientTechnicalException(String message) {
        super(message);
    }

    public ClientTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientTechnicalException(Throwable cause) {
        super(cause);
    }

    protected ClientTechnicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
