package com.holland.kit.db;

import com.holland.kit.base.ObjectPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class JDBCPool extends ObjectPool<Connection> {
    protected String  driverClassName;
    protected String  host;
    protected Integer port;
    protected String  user;
    protected String  password;
    protected String  database;
    protected String  url;

    public JDBCPool(Map<Object, Object> conf) {
        this.driverClassName = (String) conf.get("driverClassName");
        this.host = (String) conf.get("host");
        this.port = (Integer) conf.get("port");
        this.user = (String) conf.get("user");
        this.password = (String) conf.get("password");
        this.database = (String) conf.get("database");
        this.url = (String) conf.get("url");
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
        try (Connection connection = checkOut()) {
            return SqlKit.exec(connection, sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
