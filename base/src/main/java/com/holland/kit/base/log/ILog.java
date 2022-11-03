package com.holland.kit.base.log;

/**
 * 统一的日志门面
 * 自定义参数使用 {} 占位
 */
public interface ILog {

    void trace(String msg, Object... args);

    void debug(String msg, Object... args);

    void info(String msg, Object... args);

    void warn(String msg, Object... args);

    /**
     * @param args 末尾传 Throwable 自动打印栈信息
     */
    void error(String msg, Object... args);

    /**
     * @param args 末尾传 Throwable 自动打印栈信息
     */
    void fatal(String msg, Object... args);
}
