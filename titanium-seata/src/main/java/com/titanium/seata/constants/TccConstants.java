package com.titanium.seata.constants;

public class TccConstants {
    public static final String USER_CONTEXT_KEY = "user_context";
    public static final String COMMIT_METHOD = "commit";
    public static final String ROLLBACK_METHOD = "rollback";
    public static final String PREPARE_METHOD = "prepare";

    public static final String TCC_REDIS_HOLDER = "redis";
    public static final String TCC_MEMORY_HOLDER = "memory";
    public static final String TCC_MYSQL_HOLDER = "mysql";
}
