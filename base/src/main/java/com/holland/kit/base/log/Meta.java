package com.holland.kit.base.log;

import com.holland.kit.base.conf.YamlKit;
import com.holland.kit.base.functional.Either;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Meta {
    Class<?> clazz;
    Level    level;
    String   formatter;

    private static volatile Meta instance;

    private Meta() {
    }

    static Meta getInstance() {
        if (instance == null) {
            synchronized (Meta.class) {
                if (instance == null) {
                    YamlKit.getInstance().read(".", "log_bak.yml", true)
                            .then(conf -> {
                                // 读取配置文件、实例化Meta对象
                                Level level;
                                String formatter;
                                try {
                                    //noinspection unchecked
                                    Map<String, ?> params = (Map<String, ?>) conf.get("com.holland.kit.base.log");
                                    level = Level.valueOf((String) params.get("level"));
                                    formatter = (String) params.get("formatter");
                                } catch (Exception e) {
                                    return Either.error(e);
                                }
                                instance = new Meta();
                                instance.level = level;
                                instance.formatter = formatter;
                                return Either.success(instance);
                            })
                            .peek(meta -> {
                                // 构建formatter模板
                                // TODO: 3/11/2022 这个在调用时去做，不然栈用的是生成时的栈
                                Pattern compile = Pattern.compile("%[a-zA-Z]");
                                StringBuilder builder = new StringBuilder();
                                Matcher matcher = compile.matcher(meta.formatter);
                                int start = 0;
                                while (matcher.find()) {
                                    int _start = matcher.start();
                                    int _end = matcher.end();
                                    builder.append(meta.formatter, start, _start);
                                    String key = meta.formatter.substring(_start + 1, _end);
                                    builder.append(keyFuncMap.get(key).get());
                                    start = _end;
                                }
                                builder.append(meta.formatter.substring(start));
                                meta.formatter = builder.toString();
                            })
                            .end(e -> {
                                e.printStackTrace();
                                System.exit(-1);
                            }, meta -> {
                            });

                }
            }
        }
        return instance;
    }

    public static Meta clone(Class<?> clazz) {
        Meta proto = getInstance();
        Meta target = new Meta();
        target.clazz = clazz;
        target.level = proto.level;
        target.formatter = proto.formatter;
        return target;
    }

    private static final Map<String, Supplier<String>> keyFuncMap = new HashMap<>(32);

    static {
        // TODO 考虑特化处理
        // %m 输出代码中指定的讯息，如log(message)中的message
        keyFuncMap.put("m", () -> "out");
        // %C 输出Logger所在类的名称，通常就是所在类的全名
//        keyFuncMap.put("C", () -> log.getClass().getName());

        // %c 输出所属的类目，通常就是所在类的全名
        keyFuncMap.put("c", () -> Thread.currentThread().getStackTrace()[1].getClassName());
        // %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy MMM dd HH:mm:ss , SSS}，%d{ABSOLUTE}，%d{DATE}
        keyFuncMap.put("d", () -> new Date().toString());
        // %F 输出所在类的类名称，只有类名。
        keyFuncMap.put("F", () -> {
            String fullName = Thread.currentThread().getStackTrace()[1].getClassName();
            return fullName.substring(fullName.lastIndexOf('.') + 1);
        });
        // %l 输出语句所在的行数，包括类名+方法名+文件名+行数
        keyFuncMap.put("l", () -> Thread.currentThread().getStackTrace()[7].toString());
        // %L 输出语句所在的行数，只输出数字
        keyFuncMap.put("L", () -> Thread.currentThread().getStackTrace()[1].getLineNumber() + "");
        // %M 输出方法名
        keyFuncMap.put("M", () -> Thread.currentThread().getStackTrace()[1].getMethodName());
        // %p 输出日志级别，即DEBUG，INFO，WARN，ERROR，FATAL
        keyFuncMap.put("p", () -> Meta.getInstance().level.name());
        // %r 输出自应用启动到输出该log信息耗费的毫秒数
        keyFuncMap.put("r", () -> System.currentTimeMillis() + "");
        // %t 输出产生该日志事件的线程名
        keyFuncMap.put("t", () -> Thread.currentThread().getName());
        // %n 输出一个回车换行符，Windows平台为“/r/n”，Unix平台为“/n”
        keyFuncMap.put("n", () -> "\r\n");
    }
}
