package com.xxzhwx.db;

/**
 * Created by xxzhwx.
 */
public interface ParamsBuilder<T> {
    Object[] build(T t);
}
