package com.holland.kit.db;

import com.holland.kit.base.conf.YamlKit;
import com.holland.kit.base.functional.Either;

import java.util.List;
import java.util.Map;

public class Commander {
    public static void main(String[] args) {
        Either<Throwable, Map<String, Object>> read = YamlKit.getInstance().read("", "datasource.yml", false);
        MysqlPool pool = new MysqlPool(read.t);

        List<Map<String, ?>> exec = pool.exec("select * from sys_user limit 10");
        System.out.println(exec);
    }
}
