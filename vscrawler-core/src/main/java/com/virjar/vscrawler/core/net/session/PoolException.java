package com.virjar.vscrawler.core.net.session;

/**
 * Created by virjar on 17/6/3.
 */
public class PoolException extends RuntimeException {
    public PoolException() {
    }

    public PoolException(Throwable cause) {
        super(cause);
    }

    public PoolException(String message) {
        super(message);
    }

    public PoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
