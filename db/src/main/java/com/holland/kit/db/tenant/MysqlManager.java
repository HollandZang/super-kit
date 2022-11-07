package com.holland.kit.db.tenant;

import com.alibaba.fastjson.JSONArray;
import com.holland.kit.base.JsonX;
import com.holland.kit.base.file.YamlKit;
import com.holland.kit.base.functional.Either;
import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;
import com.holland.kit.db.MysqlPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlManager {
    private static final ILog log = LogFactory.create(MysqlManager.class);

    private static final String DEFAULT_KEY = "DEFAULT_KEY";

    private static volatile MysqlManager instance;

    private final Map<String, MysqlPool> pools;

    private MysqlManager(int initialCapacity) {
        this.pools = new HashMap<>(initialCapacity);
    }

    public static MysqlManager getInstance() {
        if (instance == null) {
            synchronized (MysqlManager.class) {
                if (instance == null) {
                    YamlKit.getInstance().read(".", "datasource.yml", false)
                            .then(read -> {
                                JsonX confList = new JsonX(read.get("com.holland.kit.db"));
                                int   size     = ((JSONArray) confList.resource).size();
                                instance = new MysqlManager(size);
                                for (int i = 0; i < size; i++) {
                                    Map<String, Object> conf = confList.find("[0].conf");
                                    String              key  = conf.getOrDefault("key", DEFAULT_KEY).toString();
                                    if (instance.pools.containsKey(key))
                                        return Either.error(new RuntimeException("Duplicate key: " + key));
                                    instance.pools.put(key, new MysqlPool(conf));
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

    public MysqlPool use(String key) {
        return pools.get(key);
    }

    public List<Map<String, ?>> exec(String sql, Object... params) {
        return use(DEFAULT_KEY).exec(sql, params);
    }
}
