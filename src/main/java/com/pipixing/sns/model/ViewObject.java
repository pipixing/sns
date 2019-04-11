package com.pipixing.sns.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
    private Map<String, Object> objs = new HashMap<>();

    public void set(String key, Object values){
        objs.put(key, values);
    }

    public Object get(String key){
        return objs.get(key);
    }

}
