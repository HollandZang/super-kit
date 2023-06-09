package com.holland.kit.db.sqlite.sqlite3;

import com.alibaba.fastjson2.JSON;
import com.holland.kit.base.functional.Either;
import com.holland.kit.db.sqlite.Sqlite;
import com.holland.kit.db.sqlite.SqlitePool;
import com.holland.kit.db.sqlite.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sqlite3 implements Sqlite {
    @Override
    public void checkEnv() throws Exception {

    }

    @Override
    public boolean existTable() {
        return false;
    }

    @Override
    public Either<RuntimeException, Object> createTableWrapper() {
        return null;
    }

    @Override
    public String find(String command) {
        return null;
    }

    public final static SqlitePool sqlitePool = getSqlitePool();

    public static void main(String[] args) throws SQLException, URISyntaxException, IOException {

        Test one = sqlitePool.one(Test.class, "SELECT * FROM Test limit 1");
        System.out.println(JSON.toJSONString(one));

        List<Test> more = sqlitePool.more(Test.class, "SELECT * FROM Test");
        System.out.println(JSON.toJSONString(more));
    }

    private static SqlitePool getSqlitePool() {
        Map<String, Object> conf = new HashMap<>();
        conf.put("key", "DEFAULT_KEY");
        conf.put("driverClassName", "org.sqlite.JDBC");
        conf.put("url", "jdbc:sqlite:super-kit-db.db");
        SqlitePool sqlitePool = new SqlitePool(conf);
        return sqlitePool;
    }
}
