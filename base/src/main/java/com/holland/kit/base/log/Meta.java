package com.holland.kit.base.log;

import com.holland.kit.base.JsonX;
import com.holland.kit.base.Pair;
import com.holland.kit.base.Trie;
import com.holland.kit.base.file.YamlKit;
import com.holland.kit.base.functional.Either;

import java.util.*;
import java.util.stream.Collectors;

public class Meta {
    Level                            level;
    Trie<String, Level>              levels; // 根据类名获取不同的日志级别
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
                    YamlKit.getInstance().read(".", "log.yml", true)
                            .then(conf -> {
                                // 读取配置文件、实例化Meta对象
                                Level                            level;
                                Trie<String, Level>              levels;
                                String                           formatter,                date_time_formatter;
                                Map<Level, Pair<String, String>> color_formatter;
                                try {
                                    JsonX params = new JsonX(conf.get("com.holland.kit.base.log"));
                                    level = Level.valueOf(params.find("level"));
                                    formatter = params.find("template");
                                    date_time_formatter = params.find("date_time_formatter");
                                    color_formatter = loadColor(params);
                                    levels = loadLevels(params.find("levels"));
                                } catch (Exception e) {
                                    return Either.error(e);
                                }
                                instance = new Meta();
                                instance.level = level;
                                instance.levels = levels;
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

    private static Trie<String, Level> loadLevels(Map<String, String> levels) {
        return Trie.create(
                levels.entrySet().stream().map(e -> {
                    List<String> keys  = Arrays.asList(e.getKey().split("\\."));
                    Level        value = Level.valueOf(e.getValue());
                    return new Pair<>(keys, value);
                }).collect(Collectors.toList())
        );
    }

    private static Map<Level, Pair<String, String>> loadColor(JsonX params) {
        Map<Level, Pair<String, String>> container = new HashMap<>(Level.values().length - 2, 1);

        Map<String, String> color_fonts       = params.find("color_fonts");
        Map<String, String> color_backgrounds = params.find("color_backgrounds");
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
        Meta proto  = getInstance();
        Meta target = new Meta();
        target.level = proto.level;
        // 读取字典树
        String[]            keys = clazz.getName().split("\\.");
        Trie<String, Level> trie = proto.levels.findNode(keys);
        if (trie == null) {
            int len = keys.length;
            while (trie == null) {
                if (--len <= 1)
                    break;
                String[] newKeys = new String[len];
                System.arraycopy(keys, 0, newKeys, 0, len);
                trie = proto.levels.findNode(newKeys);
            }
        }
        if (trie != null && trie.dataSet != null) {
            trie.dataSet.stream().findFirst().ifPresent(level -> target.level = level);
        }
        target.clazz = clazz;
        target.template = proto.template;
        target.date_time_formatter = proto.date_time_formatter;
        target.color_formatter = proto.color_formatter;
        target.timer = instance.timer;
        return target;
    }
}
