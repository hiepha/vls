package com.vlls.exception;

/**
 * Created by hiephn on 2014/07/14.
 */
public class InvalidBarcodeException extends InvalidRequestItemException {
    private static final long serialVersionUID = 1L;
    public InvalidBarcodeException() {
    }

    public InvalidBarcodeException(String message) {
        super(message);
    }

    public InvalidBarcodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBarcodeException(Throwable cause) {
        super(cause);
    }

    public InvalidBarcodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
