package com.vlls.exception;

/**
 * Created by hiephn on 2014/07/14.
 */
public class EmailExistException extends DuplicatedItemException {
    private static final long serialVersionUID = 1L;
    public EmailExistException() {
    }

    public EmailExistException(String message) {
        super(message);
    }

    public EmailExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailExistException(Throwable cause) {
        super(cause);
    }

    public EmailExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
