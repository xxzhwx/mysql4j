package com.xxzhwx.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by xxzhwx.
 */
public class StandaloneDbManager implements DbManager {
    private final DbNode db;

    public StandaloneDbManager(DbNodeConfig config) {
        db = new DruidDbNode(config);
    }

    @Override
    public Connection getConnection() {
        try {
            return db.getConnection();
        } catch (SQLException e) {
            throw new DbException("get database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            db.destroy();
        } catch (SQLException e) {
            throw new DbException("destroy database failed: " + e.getMessage());
        }
    }

    @Override
    public <T> ArrayList<T> executeQuery_ObjectList(String sql, Object[] params, ResultObjectBuilder<T> builder) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<T> objectList = null;

        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();
            objectList = getObjectListFromResultSet(rs, builder);
        } catch (SQLException e) {
            throw new DbException("execute query failed: " + e.getMessage());
        } finally {
            releaseDbResource(rs, stmt, conn);
        }

        return objectList;
    }

    @Override
    public int executeUpdate(String sql, Object[] params) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        int rowCount = 0;

        try {
            stmt = prepareStatement(conn, sql, params);
            rowCount = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("execute update failed: " + e.getMessage());
        } finally {
            releaseDbResource(null, stmt, conn);
        }

        return rowCount;
    }

    @Override
    public <T> int[] executeBatch(String sql, Collection<T> valueList, ParamsBuilder<T> builder) {
        Collection<Object[]> paramsList = new ArrayList<>(valueList.size());
        for (T t : valueList) {
            Object[] params = builder.build(t);
            paramsList.add(params);
        }

        return executeBatch(sql, paramsList);
    }

    @Override
    public int[] executeBatch(String sql, Collection<Object[]> paramsList) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        int[] rowCounts = null;

        try {
            conn.setAutoCommit(false);

            stmt = prepareBatchStatement(conn, sql, paramsList);
            rowCounts = stmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new DbException("rollback failed: " + e1.getMessage());
            }

            throw new DbException("execute batch failed: " + e.getMessage());
        } finally {
            releaseDbResource(null, stmt, conn);
        }
        return rowCounts;
    }

    private static PreparedStatement prepareStatement(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        if (params != null) {
            setParams(stmt, params);
        }
        return stmt;
    }

    private static PreparedStatement prepareBatchStatement(Connection conn, String sql, Collection<Object[]> paramsList) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        if (paramsList != null) {
            for (Object[] params : paramsList) {
                setParams(stmt, params);
                stmt.addBatch();
            }
        }
        return stmt;
    }

    private static void setParams(PreparedStatement stmt, Object[] params) throws SQLException {
        Object o;
        for (int i = 0, n = params.length; i < n; ++i) {
            o = params[i];

            if (o instanceof Integer) {
                stmt.setInt(i + 1, (Integer) o);
            } else if (o instanceof Short) {
                stmt.setShort(i + 1, (Short) o);
            } else if (o instanceof Long) {
                stmt.setLong(i + 1, (Long) o);
            } else if (o instanceof String) {
                stmt.setString(i + 1, (String) o);
            } else if (o instanceof Date) {
                stmt.setObject(i + 1, o);
            } else if (o instanceof Boolean) {
                stmt.setBoolean(i + 1, (Boolean) o);
            } else if (o instanceof byte[]) {
                stmt.setBytes(i + 1, (byte[]) o);
            } else if (o instanceof Double) {
                stmt.setDouble(i + 1, (Double) o);
            } else if (o instanceof Float) {
                stmt.setFloat(i + 1, (Float) o);
            } else if (o == null) {
                stmt.setNull(i + 1, Types.OTHER);
            } else {
                throw new SQLException("Unknown database data type");
            }
        }
    }

    private static <T> ArrayList<T> getObjectListFromResultSet(ResultSet rs, ResultObjectBuilder<T> builder) throws SQLException {
        if (rs == null) {
            return null;
        }

        ArrayList<T> objectList = new ArrayList<>();
        while (rs.next()) {
            objectList.add(builder.build(rs));
        }
        return objectList;
    }

    private static void releaseDbResource(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
