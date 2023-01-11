package com.holland.kit.base.log;


import com.holland.kit.base.file.YamlKit;
import com.holland.kit.base.functional.Either;

import java.util.Map;

public class LogFactory {

    public LogType LOG_TYPE;
    private static volatile LogFactory instance;

    private LogFactory() {
        Either<Throwable, Map<String, Object>> read = YamlKit.getInstance().read(".", "log.yml", true);
        Map<String, Object> conf = read.t;
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

    public static Log create(Class<?> clazz) {
        return getInstance().LOG_TYPE.create(Meta.getInstance().clone(clazz));
    }

    public static Log create(String name) {
        return getInstance().LOG_TYPE.create(Meta.getInstance().clone(name));
    }

    private enum LogType {
        STANDARD_LOG() {
            @Override
            public Log create(Meta meta) {
                return new StandardLog(meta);
            }
        };

        public abstract Log create(Meta meta);
    }
}
