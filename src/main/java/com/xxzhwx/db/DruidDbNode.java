package com.xxzhwx.db;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xxzhwx.
 */
public class DruidDbNode implements DbNode {
    private DruidDataSource pool;

    public DruidDbNode(DbNodeConfig config) {
        this.pool = createConnectionPool(config);
    }

    private static DruidDataSource createConnectionPool(DbNodeConfig config) {
        DruidDataSource ds = new DruidDataSource();

        try {
            ds.setDriverClassName(config.driver);
        } catch (Exception e) {
            String errMsg = "Invalid driver: " + config.driver;
            System.err.println(errMsg);
            System.err.println(e.getMessage());
            throw new DbException(errMsg);
        }

        String jdbcUrl = String.format("jdbc:mysql://%s/%s?user=%s&password=%s&autoReconnect=true" +
                        "&useServerPreparedStmts=false&rewriteBatchedStatements=true",
                config.ip, config.dbName, config.user, config.password);
        ds.setUrl(jdbcUrl);
        ds.setInitialSize(config.poolMin); // 初始化大小
        ds.setMinIdle(config.poolMin);     // 最小
        ds.setMaxActive(config.poolMax);   // 最大
        ds.setMaxWait(config.checkoutTimeout); // 获取连接等待超时时间

        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);

        // 间隔多久检测一次需要关闭的空闲连接，单位是毫秒
        ds.setTimeBetweenEvictionRunsMillis(60000);
        // 一个连接在池中的最小生存时间，单位是毫秒
        ds.setMinEvictableIdleTimeMillis(300000);
        // 如果是Oracle，则把poolPreparedStatements配置为true；mysql可以配置为false。分库分表较多的数据库建议配置为false。
        ds.setPoolPreparedStatements(false);
        ds.setValidationQuery("SELECT 1");
        return ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    @Override
    public void destroy() throws SQLException {
        pool.close();
    }
}
