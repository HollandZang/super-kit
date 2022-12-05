package com.holland.kit.db.tenant;

import com.alibaba.fastjson.JSONArray;
import com.holland.kit.base.JsonX;
import com.holland.kit.base.file.YamlKit;
import com.holland.kit.base.functional.Either;
import com.holland.kit.base.log.Log;
import com.holland.kit.base.log.LogFactory;
import com.holland.kit.db.JDBCPool;
import com.holland.kit.db.MysqlPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseManager {
    private static final Log log = LogFactory.create(DataBaseManager.class);

    private static final String DEFAULT_KEY = "DEFAULT_KEY";

    private static volatile DataBaseManager instance;

    public final Map<String, JDBCPool> pools;

    private DataBaseManager(int initialCapacity) {
        this.pools = new HashMap<>(initialCapacity);
    }

    public static DataBaseManager getInstance() {
        if (instance == null) {
            synchronized (DataBaseManager.class) {
                if (instance == null) {
                    YamlKit.getInstance().read(".", "datasource.yml", false)
                            .then(read -> {
                                JsonX confList = new JsonX(read.get("com.holland.kit.db"));
                                int   size     = ((JSONArray) confList.resource).size();
                                instance = new DataBaseManager(size);
                                for (int i = 0; i < size; i++) {
                                    Map<String, Object> conf = confList.find("[" + i + "]");
                                    String              key  = conf.getOrDefault("key", DEFAULT_KEY).toString();
                                    if (instance.pools.containsKey(key))
                                        return Either.error(new RuntimeException("Duplicate key: " + key));

                                    Object driverClassName = conf.get("driverClassName");
                                    if ("com.mysql.cj.jdbc.Driver".equals(driverClassName) || "com.mysql.jdbc.Driver".equals(driverClassName)) {
                                        instance.pools.put(key, new MysqlPool(conf));
                                    } else {
                                        return Either.error(new RuntimeException("Not support driver: " + driverClassName));
                                    }
                                }
                                return Either.success(instance);
                            })
                            .end(e -> {
                                log.fatal("THE BOOT ITEM IS ABNORMAL!", e);
                                System.exit(-1);
                            }, manager -> {
                            });
                }
            }
        }
        return instance;
    }

    public JDBCPool use(String key) {
        return pools.get(key);
    }

    public List<Map<String, ?>> exec(String sql, Object... params) {
        return use(DEFAULT_KEY).exec(sql, params);
    }
}
