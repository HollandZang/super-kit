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

        String sqlList = "alter table sys_msg\n" +
                "    add fbdw_dm varchar(12) null comment '发布单位代码';\n" +
                "\n" +
                "alter table sys_msg\n" +
                "    add fbdw_mc varchar(64) null comment '发布单位名称';";

        dataBaseManager.pools.forEach((s, pool) -> {
            for (String sql : sqlList.split(";")) {
                List<Map<String, ?>> exec = pool.execIgnoreException(sql);
                log.fatal("{} -> res:{}", s, exec);
            }
        });
    }
}
