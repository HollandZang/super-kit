package com.holland.kit.base.log;


import com.holland.kit.base.conf.YamlKit;
import com.holland.kit.base.functional.Either;

import java.util.Map;

public class LogFactory {

    public                  LogType    LOG_TYPE;
    private static volatile LogFactory instance;

    private LogFactory() {
        Either<Throwable, Map<String, Object>> read = YamlKit.getInstance().read(".", "log_bak.yml", true);
        Map<String, Object>                    conf = read.t;
        //noinspection unchecked
        Object o = ((Map<String, ?>) conf.get("com.holland.kit.base.log")).get("type");
        this.LOG_TYPE = LogType.valueOf((String) o);
    }

    private static LogFactory getInstance() {
        if (instance == null) {
            synchronized (LogFactory.class) {
                if (instance == null) {
                    instance = new LogFactory();
                }
            }
        }
        return instance;
    }

    public static ILog create(Class<?> clazz) {
        return getInstance().LOG_TYPE.create(clazz, Meta.getInstance().clone(clazz));
    }

    private enum LogType {
        STANDARD_LOG() {
            @Override
            public <T> ILog create(Class<T> clazz, Meta meta) {
                return new StandardLog(meta);
            }
        };

        public abstract <T> ILog create(Class<T> clazz, Meta meta);
    }
}
