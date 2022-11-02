package com.holland.kit.base.log;


public class LogFactory {

    public                  LogType    LOG_TYPE;
    public                  MetaType   META_TYPE;
    private static volatile LogFactory instance;

    private LogFactory() {
        // todo 从配置表读取日志工厂信息
        this.LOG_TYPE = LogType.STANDARD_LOG;
        this.META_TYPE = MetaType.FROM_CODE;
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
        return getInstance().LOG_TYPE.create(clazz);
    }

    public enum LogType {
        STANDARD_LOG() {
            @Override
            public <T> ILog create(Class<T> clazz) {
                return new StandardLog(getInstance().META_TYPE.create(clazz));
            }
        };

        public abstract <T> ILog create(Class<T> clazz);
    }

    public enum MetaType {
        FROM_CODE() {
            @Override
            public <T> Meta create(Class<T> clazz) {
                Meta meta = new Meta();
                meta.clazz = clazz;
                meta.level = Level.INFO;
                return meta;
            }
        };

        public abstract <T> Meta create(Class<T> clazz);
    }
}
