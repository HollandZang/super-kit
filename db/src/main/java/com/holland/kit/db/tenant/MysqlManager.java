package com.holland.kit.db.tenant;

import com.holland.kit.base.conf.YamlKit;
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
                                //noinspection unchecked
                                List<Map<String, Object>> confList = ((List<Map<String, Object>>) read.get("com.holland.kit.db"));
                                instance = new MysqlManager(confList.size());
                                for (Map<String, Object> map : confList) {
                                    //noinspection unchecked
                                    Map<String, Object> conf = (Map<String, Object>) map.get("conf");
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
