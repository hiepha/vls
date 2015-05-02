package com.vlls.exception;

/**
 * Created by hiephn on 2014/07/14.
 */
public class PhoneExistException extends DuplicatedItemException {
    private static final long serialVersionUID = 1L;
    public PhoneExistException() {
    }

    public PhoneExistException(String message) {
        super(message);
    }

    public PhoneExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneExistException(Throwable cause) {
        super(cause);
    }

    public PhoneExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
