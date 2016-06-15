package com.xxzhwx.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xxzhwx.
 */
@FunctionalInterface
public interface ResultObjectBuilder<T> {
    T build(ResultSet rs) throws SQLException;
}
