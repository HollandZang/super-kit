package com.holland.kit.db;

import com.holland.kit.base.log.Log;
import com.holland.kit.base.log.LogFactory;
import com.holland.kit.db.tenant.DataBaseManager;

import java.util.List;
import java.util.Map;

public class Commander {
    private static final Log log = LogFactory.create(Commander.class);

    public static void main(String[] args) throws InterruptedException {
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();

        String sqlList = "select 1 ;";

        dataBaseManager.pools.forEach((s, pool) -> {
            for (String sql : sqlList.split(";")) {
                List<Map<String, ?>> exec = pool.execIgnoreException(sql);
                log.fatal("{} -> res:{}", s, exec);
            }
        });

    }
}
