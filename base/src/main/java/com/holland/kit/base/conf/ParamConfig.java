package com.holland.kit.base.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置参数的缓存
 */
public class ParamConfig {
    private static volatile ParamConfig instance;

    private ParamConfig() {
        caches = new HashMap<>();
    }

    public static ParamConfig getInstance() {
        if (instance == null) {
            synchronized (ParamConfig.class) {
                if (instance == null) {
                    instance = new ParamConfig();
                }
            }
        }
        return instance;
    }

    private final Map<String, Object> caches;

    public void put(Map<String, Object> conf) {
        conf.forEach((k, v) -> {
            if (v instanceof Iterable) {

            } else if (v instanceof Map) {
                // TODO
            } else {
                put(k, v);
            }
        });
    }

    public void put(String key, Object value) {
        caches.put(key, value);
    }

    public void putIterable(Iterable iterable, String prefix) {
//        iterable.forEach(value);
//        caches.put(key, value);
    }

    public Object get(String key) {
        return caches.get(key);
    }

    public String getStr(String key) {
        return null;
    }

    public Integer getInt(String key) {
        return null;
    }

    public Boolean getBool(String key) {
        return null;
    }
}
