package com.holland.kit.db;

import com.holland.kit.base.ObjectPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class JDBCPool extends ObjectPool<Connection> {
    protected String  driverClassName;
    protected String  host;
    protected Integer port;
    protected String  user;
    protected String  password;
    protected String  database;
    protected String  url;

    public JDBCPool(Map<String, Object> conf) {
        super((Integer) conf.getOrDefault("corePoolSize", 1)
                , (Integer) conf.getOrDefault("maximumPoolSize", 10)
                , (Integer) conf.getOrDefault("keepAliveTime", 5L)
                , TimeUnit.valueOf((String) conf.getOrDefault("unit", TimeUnit.MINUTES.name())));

        this.driverClassName = (String) conf.get("driverClassName");
        this.host = (String) conf.get("host");
        this.port = (Integer) conf.get("port");
        this.user = (String) conf.get("user");
        this.password = (String) conf.get("password");
        this.database = (String) conf.get("database");
        this.url = (String) conf.get("url");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Close connection: host[{}] port[{}] database[{}]", host, port, database);
            super.close();
            log.debug("Closed connection: host[{}] port[{}] database[{}]", host, port, database);
        }));
    }

    @Override
    protected Connection create() {
        try {
            log.trace("Create a connection: host[{}] port[{}] database[{}]", host, port, database);
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean validate(Connection o) {
        try {
            return !o.isClosed();
        } catch (SQLException e) {
            log.trace("A connection can not used: host[{}] port[{}] database[{}]", host, port, database);
            expire(o);
            return false;
        }
    }

    @Override
    protected void expire(Connection o) {
        try {
            log.trace("Expire a connection: host[{}] port[{}] database[{}]", host, port, database);
            o.close();
        } catch (SQLException e) {
            log.error("Close connection error", e);
        } finally {
            o = null;
        }
    }

    public List<Map<String, ?>> exec(String sql, Object... params) {
        Connection connection = checkOut();
        try {
            return SqlKit.exec(connection, sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            checkIn(connection);
        }
    }

    public List<Map<String, ?>> execIgnoreException(String sql, Object... params) {
        Connection connection = checkOut();
        try {
            return SqlKit.exec(connection, sql, params);
        } catch (SQLException e) {
            log.warn("Sql exec err: host[{}] port[{}] database[{}]", host, port, database, e);
            return new ArrayList<>();
        } finally {
            checkIn(connection);
        }
    }
}
