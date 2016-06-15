package com.xxzhwx.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xxzhwx.
 */
public interface DbNode {
    Connection getConnection() throws SQLException;

    void destroy() throws SQLException;
}
