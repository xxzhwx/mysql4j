package com.xxzhwx;

import com.xxzhwx.db.*;

import java.util.ArrayList;

/**
 * Show how to use DbManager to manipulate database.
 */
public class App {
    private static final String SELECT_USER = "SELECT * FROM user WHERE `id`=?";
    private static final String UPDATE_USER = "UPDATE user SET name=? WHERE `id`=?";

    public static void main(String[] args) {
        DbNodeConfig config = new DbNodeConfig();
        config.driver = "com.mysql.jdbc.Driver";
        config.ip = "127.0.0.1";
        config.dbName = "test";
        config.user = "root";
        config.password = "";
        config.poolMin = 1;
        config.poolMax = 3;
        config.checkoutTimeout = 12000;

        DbManager dbMgr = new StandaloneDbManager(config);
        ArrayList<User> users = dbMgr.executeQuery_ObjectList(SELECT_USER,
                new Object[]{1}, User.RESULT_BUILDER);
        for (User user : users) {
            System.out.println("user: " + user.id + "," + user.name);
            dbMgr.executeUpdate(UPDATE_USER, new Object[]{user.name+"1", user.id});
        }

        // 下面的语句执行后会覆盖上面的更新结果，因为上面的更新执行后没有再次查询并更新 users 列表
        dbMgr.executeBatch(UPDATE_USER, users, User.PARAMS_BUILDER);

        dbMgr.destroy();
    }

    private static class User {
        private long id;
        private String name;

        public static final ParamsBuilder<User> PARAMS_BUILDER = user -> {
            Object[] params = new Object[2];
            params[0] = user.name + "2";
            params[1] = user.id;
            return params;
        };

        public static final ResultObjectBuilder<User> RESULT_BUILDER = rs -> {
            User user = new User();
            user.id = rs.getLong("id");
            user.name = rs.getString("name");
            return user;
        };
    }
}
