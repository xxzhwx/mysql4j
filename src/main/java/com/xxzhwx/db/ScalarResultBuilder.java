package com.xxzhwx.db;

import java.util.Date;

/**
 * Created by xxzhwx.
 */
public final class ScalarResultBuilder {
    public final static ResultObjectBuilder<Integer> intResultBuilder;
    public final static ResultObjectBuilder<Date> dateResultBuilder;
    public final static ResultObjectBuilder<String> stringResultBuilder;

    static {
        intResultBuilder = rs -> rs.getInt(1);
        dateResultBuilder = rs -> rs.getTimestamp(1);
        stringResultBuilder = rs -> rs.getString(1);
    }
}
