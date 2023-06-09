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

        String sqlList = "ALTER TABLE `zb_gymckjl` ADD COLUMN `zrr_sfzh` varchar(18) NULL COMMENT '责任人身份证号码' AFTER `remake`,ADD COLUMN `zrr_xm` varchar(50) NULL COMMENT '责任人姓名' AFTER `zrr_sfzh`;";

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
