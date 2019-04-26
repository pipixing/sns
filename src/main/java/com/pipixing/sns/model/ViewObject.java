package com.pipixing.sns.model;

import java.util.HashMap;
import java.util.Map;
//用于和前端交互的对象
public class ViewObject {
    private Map<String, Object> objs = new HashMap<>();

    public void set(String key, Object values){
        objs.put(key, values);
    }

    public Object get(String key){
        return objs.get(key);
    }

}
