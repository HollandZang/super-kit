package com.holland.kit.base.log;

/**
 * 控制台标准打印日志
 */
public class StandardLog extends BaseLog implements Log {

    public StandardLog(Meta meta) {
        super(meta);
    }

    @Override
    public void trace(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> System.out.println(buildFormatter(Level.TRACE, msg, args)));
    }

    @Override
    public void debug(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> System.out.println(buildFormatter(Level.DEBUG, msg, args)));
    }

    @Override
    public void info(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> System.out.println(buildFormatter(Level.INFO, msg, args)));
    }

    @Override
    public void warn(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> System.out.println(buildFormatter(Level.WARN, msg, args)));
    }

    @Override
    public void error(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> System.out.println(buildFormatter(Level.ERROR, msg, args)));
    }

    @Override
    public void fatal(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> System.out.println(buildFormatter(Level.FATAL, msg, args)));
    }
}
