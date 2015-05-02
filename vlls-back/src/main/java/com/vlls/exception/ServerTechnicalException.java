package com.vlls.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hiephn on 2014/07/12.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerTechnicalException extends Exception {
    private static final long serialVersionUID = 1L;
    public ServerTechnicalException() {
        super();
    }

    public ServerTechnicalException(String message) {
        super(message);
    }

    public ServerTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerTechnicalException(Throwable cause) {
        super(cause);
    }

    protected ServerTechnicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
