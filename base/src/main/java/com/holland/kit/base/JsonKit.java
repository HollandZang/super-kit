package com.holland.kit.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonKit {
    public static void main(String[] args) {
        String s = "{\"cljg\":\"测试\",\"cltp\":\"\",\"id\":\"c46590ca95d34d368be39980cd7ea989\",\"clzt\":\"5\"}";
        JSONObject jsonObject = JSON.parseObject(s);
        jsonObject.forEach((s1, o) -> System.out.printf("%s=%s&",s1,o));
    }
}
