package com.holland.kit.base.log;

import com.holland.kit.base.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class BaseLog implements Log {

    private static final   Map<String, Fn_Formatter> KEY_FUNC          = new HashMap<>(32);
    private static final   int                       IDX               = 6;
    protected static final Pattern                   COMPILE_FORMATTER = Pattern.compile("%(className|[a-zA-Z])");
    protected static final Pattern                   COMPILE_MSG       = Pattern.compile("\\{}");
    protected final        Meta                      meta;

    public BaseLog(Meta meta) {
        this.meta = meta;
    }

    protected String buildFormatter(Level callLevel, String msg, Object... args) {
        String _msg = builderMsg(callLevel, msg, args);

        // 构建formatter模板
        StringBuilder builder = new StringBuilder();
        Matcher       matcher = COMPILE_FORMATTER.matcher(meta.template);
        int           start   = 0;
        while (matcher.find()) {
            int _start = matcher.start();
            int _end   = matcher.end();
            builder.append(meta.template, start, _start);
            String key = meta.template.substring(_start + 1, _end);
            builder.append(KEY_FUNC.get(key).build(callLevel, this, _msg));
            start = _end;
        }
        builder.append(meta.template.substring(start));
        String content = builder.toString();

        Pair<String, String> color_pair = meta.color_formatter.get(callLevel);
        return color_pair == null
                ? content
                : color_pair.left + content + color_pair.right;
    }

    protected String builderMsg(Level callLevel, String msg, Object... args) {
        if (null == msg) return null;
        if (args.length == 0) return msg;
        StringBuilder builder = new StringBuilder();
        Matcher       matcher = COMPILE_MSG.matcher(msg);
        int           start   = 0, idx = 0;
        while (matcher.find()) {
            int _start = matcher.start();
            int _end   = matcher.end();
            builder.append(msg, start, _start);
            builder.append(args[idx++]);
            start = _end;
        }
        builder.append(msg.substring(start));
        if (callLevel.b >> 2 == 0 && idx < args.length) {
            Object exception = args[args.length - 1];
            if (exception instanceof Throwable) {
                builder.append(" ").append(exception.getClass().getSimpleName()).append(" :").append(((Throwable) exception).getMessage()).append("\r\n\t");
                builder.append(Arrays.stream(((Throwable) exception).getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\r\n\t")));
            }
        }
        return builder.toString();
    }

    @FunctionalInterface
    interface Fn_Formatter {
        String build(Level callLevel, BaseLog log, String msg);
    }

    static {
        // %className
        KEY_FUNC.put("className", (callLevel, log, msg) -> "TODO");
        // %m 输出代码中指定的讯息，如log(message)中的message
        KEY_FUNC.put("m", (callLevel, log, msg) -> msg);
        // %C 输出Logger所在类的名称，通常就是所在类的全名
        KEY_FUNC.put("C", (callLevel, log, msg) -> log.getClass().getName());
        // %c 输出所属的类目，通常就是所在类的全名
        KEY_FUNC.put("c", (callLevel, log, msg) -> Thread.currentThread().getStackTrace()[IDX].getClassName());
        // %d 输出日志时间点的日期或时间
        KEY_FUNC.put("d", (callLevel, log, msg) -> DateTimeFormatter.ofPattern(log.meta.date_time_formatter).format(LocalDateTime.now()));
        // %F 输出所在类的类名称，只有类名。
        KEY_FUNC.put("F", (callLevel, log, msg) -> {
            String fullName = Thread.currentThread().getStackTrace()[IDX].getClassName();
            return fullName.substring(fullName.lastIndexOf('.') + 1);
        });
        // %l 输出语句所在的行数，包括类名+方法名+文件名+行数
        KEY_FUNC.put("l", (callLevel, log, msg) -> Thread.currentThread().getStackTrace()[IDX].toString());
        // %L 输出语句所在的行数，只输出数字
        KEY_FUNC.put("L", (callLevel, log, msg) -> Thread.currentThread().getStackTrace()[IDX].getLineNumber() + "");
        // %M 输出方法名
        KEY_FUNC.put("M", (callLevel, log, msg) -> Thread.currentThread().getStackTrace()[IDX].getMethodName());
        // %p 输出日志级别，即DEBUG，INFO，WARN，ERROR，FATAL
        KEY_FUNC.put("p", (callLevel, log, msg) -> callLevel.name());
        // %r 输出自应用启动到输出该log信息耗费的毫秒数
        KEY_FUNC.put("r", (callLevel, log, msg) -> System.currentTimeMillis() - log.meta.timer + "");
        // %t 输出产生该日志事件的线程名
        KEY_FUNC.put("t", (callLevel, log, msg) -> Thread.currentThread().getName());
        // %n 输出一个回车换行符，Windows平台为“/r/n”，Unix平台为“/n”
        KEY_FUNC.put("n", (callLevel, log, msg) -> "\r\n");
    }
}
