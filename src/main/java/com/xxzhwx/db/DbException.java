package com.xxzhwx.db;

/**
 * Created by xxzhwx.
 */
public class DbException extends RuntimeException {
    private static final long serialVersionUID = 6687317038829865682L;

    public DbException() {
        super();
    }

    public DbException(String msg) {
        super(msg);
    }

    public DbException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
