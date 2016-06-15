package com.xxzhwx.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by xxzhwx.
 */
public interface DbManager {
    default String getName() {
        return this.getClass().getSimpleName();
    }

    Connection getConnection();

    void destroy();

    /**
     * 执行查询语句
     */
    <T> ArrayList<T> executeQuery_ObjectList(String sql, Object[] params,
                                             ResultObjectBuilder<T> builder);

    /**
     * 执行 UPDATE/INSERT/DELETE 语句或 DDL 语句
     */
    int executeUpdate(String sql, Object[] params);

    /**
     * 对一条语句执行批量操作（每次执行的参数不同），通过事务实现，任意一次执行失败将会回滚
     */
    <T> int[] executeBatch(String sql, Collection<T> valueList,
                           ParamsBuilder<T> builder);

    int[] executeBatch(String sql, Collection<Object[]> paramsList);
}
