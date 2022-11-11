package com.holland.kit.db;

import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;
import com.holland.kit.db.tenant.MysqlManager;

import java.util.List;
import java.util.Map;

public class Commander {
    private static final ILog log = LogFactory.create(Commander.class);

    public static void main(String[] args) {
        MysqlManager mysqlManager = MysqlManager.getInstance();

        List<Map<String, ?>> exec = mysqlManager.use("1").exec("select 1");

        log.fatal(exec.toString());
    }
}
