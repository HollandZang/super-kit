package com.holland.kit.base.log;

/**
 * 字颜色:30-----------39
 */
public enum ColorFronts {
    BLACK("30", "黑"),
    RED("31", "红"),
    GREEN("32", "绿"),
    YELLOW("33", "黄色"),
    BLUE("34", "蓝色"),
    VIOLET("35", "紫色"),
    BOTTLE_GREEN("36", "深绿"),
    WHITE("37", "白色"),
    ;

    public final String code;
    public final String mark;

    ColorFronts(String code, String mark) {
        this.code = code;
        this.mark = mark;
    }
}
