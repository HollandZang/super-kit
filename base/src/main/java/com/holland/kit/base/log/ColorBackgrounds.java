package com.holland.kit.base.log;

/**
 * 字背景颜色范围:40----49
 */
public enum ColorBackgrounds {
    BLACK("40", "黑"),
    RED("41", "深红"),
    GREEN("42", "绿"),
    YELLOW("43", "黄色"),
    BLUE("44", "蓝色"),
    VIOLET("45", "紫色"),
    BOTTLE_GREEN("46", "深绿"),
    WHITE("47", "白色"),
    ;

    public final String code;
    public final String mark;

    ColorBackgrounds(String code, String mark) {
        this.code = code;
        this.mark = mark;
    }
}
