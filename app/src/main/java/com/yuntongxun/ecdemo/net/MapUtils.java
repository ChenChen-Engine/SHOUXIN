package com.yuntongxun.ecdemo.net;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    private HashMap<String, Object> map = new HashMap<>();

    public MapUtils put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return map;
    }
}
