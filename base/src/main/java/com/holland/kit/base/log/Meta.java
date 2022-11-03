package com.holland.kit.base.log;

import com.holland.kit.base.Pair;
import com.holland.kit.base.Trie;
import com.holland.kit.base.conf.YamlKit;
import com.holland.kit.base.functional.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meta {
    Level                            level;
    Trie                             levels;
    String                           template;
    String                           date_time_formatter;
    Map<Level, Pair<String, String>> color_formatter;
    long                             timer;
    Class<?>                         clazz;

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
                                String formatter, date_time_formatter;
                                Map<Level, Pair<String, String>> color_formatter;
                                try {
                                    //noinspection unchecked
                                    Map<String, ?> params = (Map<String, ?>) conf.get("com.holland.kit.base.log");
                                    level = Level.valueOf((String) params.get("level"));
                                    formatter = (String) params.get("template");
                                    date_time_formatter = (String) params.get("date_time_formatter");
                                    color_formatter = loadColor(params);
                                } catch (Exception e) {
                                    return Either.error(e);
                                }
                                // TODO: 3/11/2022 配置字典树
                                instance = new Meta();
                                instance.level = level;
                                instance.template = formatter;
                                instance.date_time_formatter = date_time_formatter;
                                instance.color_formatter = color_formatter;
                                instance.timer = System.currentTimeMillis();
                                return Either.success(instance);
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

    private static Map<Level, Pair<String, String>> loadColor(Map<String, ?> params) {
        Map<Level, Pair<String, String>> container = new HashMap<>(Level.values().length - 2, 1);

        //noinspection unchecked
        Map<String, String> color_fonts = (Map<String, String>) params.get("color_fonts");
        //noinspection unchecked
        Map<String, String> color_backgrounds = (Map<String, String>) params.get("color_backgrounds");
        for (Level level : Level.values()) {
            if (Level.ALL.equals(level) || Level.OFF.equals(level))
                continue;
            List<String> conf = new ArrayList<>();
            if (color_fonts != null) {
                String ansiEnumName = color_fonts.get(level.name());
                if (ansiEnumName != null) {
                    String code = ColorFonts.valueOf(ansiEnumName).code;
                    conf.add(code);
                }
            }
            if (color_backgrounds != null) {
                String ansiEnumName = color_backgrounds.get(level.name());
                if (ansiEnumName != null) {
                    String code = ColorBackgrounds.valueOf(ansiEnumName).code;
                    conf.add(code);
                }
            }
            if (conf.size() > 0)
                container.put(level, new Pair<>("\033[" + String.join(";", conf) + 'm', "\033[0m"));
        }

        return container;
    }

    public Meta clone(Class<?> clazz) {
        Meta proto = getInstance();
        Meta target = new Meta();
        target.clazz = clazz;
        // TODO: 3/11/2022 读取字典树
        target.level = proto.level;
        target.template = proto.template;
        target.date_time_formatter = proto.date_time_formatter;
        target.color_formatter = proto.color_formatter;
        target.timer = instance.timer;
        return target;
    }
}
