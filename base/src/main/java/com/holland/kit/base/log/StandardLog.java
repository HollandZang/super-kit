package com.holland.kit.base.log;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 控制台标准打印日志
 */
public class StandardLog extends BaseLog implements ILog {

    public StandardLog(Meta meta) {
        super(meta);
    }

    @Override
    public void trace(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> {
            String format = String.format(msg.replace("{}", "%s"), args);
            System.out.println("t->" + meta.clazz.getSimpleName() + "->" + format);
        });
    }

    @Override
    public void debug(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> {
            String format = String.format(msg.replace("{}", "%s"), args);
            System.out.println("d->" + meta.clazz.getSimpleName() + "->" + format);
        });
    }

    @Override
    public void info(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> {
            String format = String.format(msg.replace("{}", "%s"), args);
            System.out.println("i->" + meta.clazz.getSimpleName() + "->" + format);
        });
    }

    @Override
    public void error(String msg, Object... args) {
        if (null == msg) return;
        meta.level.ifWrite(() -> {
            String format    = String.format(msg.replace("{}", "%s"), args);
            Object exception = args.length == 0 ? null : args[args.length - 1];
            if (exception instanceof Throwable)
                format += Arrays.stream(((Throwable) exception).getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n\t"));
            System.err.println("e->" + meta.clazz.getSimpleName() + "->" + format);
        });
    }
}
