package com.holland.kit.db;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MysqlPool extends JDBCPool {

    public MysqlPool(Map<String, Object> conf) {
        super(conf);
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            log.error("Not found driverClassName:{}", driverClassName, e);
            System.exit(-1);
        }
        url = urlParser(conf);
        log.info("Generate database connect uri: {}", url);
    }

    /**
     * 优先使用url自带的参数
     */
    private String urlParser(Map<String, Object> conf) {
        // 初始化空白参数
        Map<String, Object> paramMap = new HashMap<>();

        // 解析原有的url
        if (url != null && url.length() > 0) {
            int i = url.indexOf('?');
            String[] keys = url.substring(0, i)
                    .replaceAll("jdbc:mysql://(\\w*):(\\d*)/([_\\w]*)", "$1,$2,$3")
                    .split(",");
            host = keys[0];
            port = Integer.parseInt(keys[1]);
            database = keys[2];
            for (String kv : url.substring(i + 1).split("&")) {
                String[] kv1 = kv.split("=");
                paramMap.put(kv1[0], kv1[2]);
            }
        }

        // 注入配置文件参数
        conf.forEach(paramMap::putIfAbsent);

        // 重组url
        String url = String.format("jdbc:mysql://%1$s:%2$d/%3$s", host, port, database);
        // 重组参数
        String params = paramMap.entrySet().stream()
                .map(e -> e.getKey() + '=' + e.getValue())
                .collect(Collectors.joining("&"));

        return url + '?' + params;
    }
}
