package com.holland.kit.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class JsonX {
    public static void main(String[] args) {
        final JSONObject json = (JSONObject) JSON.parse("{a:1,data:[{uid:\"157580\",activeCid:\"69\",activeChannel:\"kuaikan\",activeGid:\"1000\",activeIP:\"2882823743\",channelUid:\"91627840_3\",status:\"0\"}]}");
        JSONObject       data = json.getJSONArray("data").getJSONObject(0);

        final JsonX jsonX = new JsonX(json);
        JSONObject  data0 = jsonX.find("data[0]");
        data0.put("uid", "new_uid");
        System.out.println(json);
    }

    public final JSON resource;

    public JsonX(Object resource) {
        if (resource instanceof JSON) this.resource = (JSON) resource;
        else if (resource instanceof String) this.resource = (JSON) JSON.parse((String) resource);
        else this.resource = (JSON) JSON.toJSON(resource);
    }

    public <T> T find(String expression) {
        return findOrDefault(expression, null);
    }

    public <T> T findOrDefault(String expression, T t) {
        T o = new Block(expression).parseActions(resource);
        return null == o ? t : o;
    }

    enum Types {
        i32, i,
        i64,
        f32, f,
        f64, d,
        bool, b,
        chr, c,
        str, s,
        list, l,
        map, m,
    }

    static class Block {
        public final Types  type;
        public final String word;

        Block(String s) {
            final String[] pair = s.split("'");
            if (pair.length == 1) {
                this.type = null;
                this.word = pair[0];
            } else {
                this.type = Types.valueOf(pair[0]);
                this.word = pair[1];
            }
        }

        Object convert(Object o) {
            if (o == null) return null;
            if (type == null) return o;
            switch (type) {
                case i32:
                case i:
                    return Integer.parseInt(o.toString());
                case i64:
                    return Long.parseLong(o.toString());
                case f32:
                case f:
                    return Float.parseFloat(o.toString());
                case f64:
                case d:
                    return Double.parseDouble(o.toString());
                case bool:
                case b:
                    return Boolean.parseBoolean(o.toString());
                case chr:
                case c:
                    return o.toString().charAt(0);
                case str:
                case s:
                    return o.toString();
                case list:
                case l:
                    return ((JSONArray) o).toJavaList(Object.class);
                case map:
                case m:
                    return ((JSONObject) o).getInnerMap();
            }
            return o;
        }

        BiFunction<Object, String, Object> supplierObj = (J, key) -> {
            final JSONObject j = (JSONObject) J;
            return j.get(key);
        };

        BiFunction<Object, String, Object> supplierArr = (J, index) -> {
            final JSONArray j = (JSONArray) J;
            return j.get(Integer.parseInt(index));
        };

        <T> T parseActions(JSON json) {
            final List<BiFunction<Object, String, Object>> actions = new ArrayList<>();
            final List<String>                             list    = new ArrayList<>();
            final char[]                                   chars   = word.toCharArray();
            final StringBuilder                            action  = new StringBuilder();
            boolean                                        arrFlag = false;
            int                                            i       = 0;
            try {
                for (; i < chars.length; i++) {
                    final char c = chars[i];
                    if (c == '[') {
                        arrFlag = true;
                        if (action.length() > 0) {
                            actions.add(supplierObj);
                            list.add(action.toString());
                            action.delete(0, action.length());
                        }
                        continue;
                    }
                    if (c == ']') {
                        if (action.length() == 0) throw new RuntimeException("cannot start with ']'");
                        if (!arrFlag) throw new RuntimeException("']' was found extra outside the array");
                        arrFlag = false;
                        actions.add(supplierArr);
                        list.add(action.toString());
                        action.delete(0, action.length());
                        continue;
                    }
                    if (c == '.') {
                        if (action.length() == 0) {
                            if (chars[i - 1] == ']') continue;
                            throw new RuntimeException("cannot start with '.'");
                        }
                        if (arrFlag) throw new RuntimeException("tried to parse the array, but found '.'");
                        actions.add(supplierObj);
                        list.add(action.toString());
                        action.delete(0, action.length());
                        continue;
                    }
                    action.append(c);
                    if (i == chars.length - 1) {
                        if (arrFlag) throw new RuntimeException("find '[', but there is no ']' until the end");
                        actions.add(supplierObj);
                        list.add(action.toString());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("syntax error, charAt " + i + " : " + chars[i], e);
            }

            Object res = json;
            try {
                i = 0;
                String word;
                for (; i < actions.size(); i++) {
                    word = list.get(i);
                    res = actions.get(i).apply(res, word);
                }
                //noinspection unchecked
                return (T) convert(res);
            } catch (Exception e) {
                throw new RuntimeException("syntax error, keyword = " + word, e);
            }
        }
    }

}
