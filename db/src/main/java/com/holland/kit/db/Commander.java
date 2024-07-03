package com.holland.kit.db;

import com.holland.kit.base.log.Log;
import com.holland.kit.base.log.LogFactory;
import com.holland.kit.db.tenant.DataBaseManager;

import java.util.List;
import java.util.Map;

public class Commander {
    private static final Log log = LogFactory.create(Commander.class);

    public static void main(String[] args) {
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();

        String sqlList = "alter table sys_config\n" +
                "   add column xjsppz      json                   null comment '支队配置：销假审批配置:{\\\"zdxj\\\":true,\\\"sfyqjlc\\\":true' after qjcns";

        dataBaseManager.pools.forEach((s, pool) -> {
//            String currSchema =(String) pool.exec("select SCHEMA() `SCHEMA`").get(0).get("SCHEMA");
//            log.info("{} -> currSchema={}", s, currSchema);
            for (String sql : sqlList.split(";")) {
                List<Map<String, ?>> exec = pool.execIgnoreException(sql);
                log.fatal("{} -> res:{}", s, exec);
            }
        });
    }
}
