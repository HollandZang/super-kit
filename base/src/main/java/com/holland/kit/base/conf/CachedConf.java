package com.holland.kit.base.conf;

import java.util.HashMap;
import java.util.Map;

public class CachedConf {
    private static volatile CachedConf instance;

    private CachedConf() {
        caches = new HashMap<>();
    }

    public static CachedConf getInstance() {
        if (instance == null) {
            synchronized (CachedConf.class) {
                if (instance == null) {
                    instance = new CachedConf();
                }
            }
        }
        return instance;
    }

    private final Map<String, Map<?, ?>> caches;

    // todo 简单的文件名软连接过来
    public void put(String fullPath, Map<?, ?> conf) {
        caches.put(fullPath, conf);
    }

    public Map<?, ?> get(String fullPath) {
        return caches.get(fullPath);
    }
}
