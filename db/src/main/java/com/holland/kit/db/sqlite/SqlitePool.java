package com.holland.kit.db.sqlite;

import com.holland.kit.db.JDBCPool;

import java.util.Map;

public class SqlitePool extends JDBCPool {
    public SqlitePool(Map<String, Object> conf) {
        super(conf);
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            log.error("Not found driverClassName:{}", driverClassName, e);
            System.exit(-1);
        }
        log.info("Generate database connect uri: {}", url);

        // 维持核心数
        for (int i = 0; i < corePoolSize; i++)
            checkIn(create());
    }
}
