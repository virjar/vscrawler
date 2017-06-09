package com.virjar.vscrawler.core.selector.xpath.exception;

/**
 * Created by virjar on 17/6/9.
 */
public class FinalTypeNotSameException extends Exception {
    public FinalTypeNotSameException() {
    }

    public FinalTypeNotSameException(Throwable cause) {
        super(cause);
    }

    public FinalTypeNotSameException(String message) {
        super(message);
    }

    public FinalTypeNotSameException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinalTypeNotSameException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
