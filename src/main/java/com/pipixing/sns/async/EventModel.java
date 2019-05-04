package com.pipixing.sns.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType type;
    private int actorId;
    private int entityType;
    //这个是载体的id
    private int entityId;
    private int entityOwner;
    private Map<String,String> exts = new HashMap<>();

    //构造函数
    public EventModel(){}
    public EventModel(EventType type){this.type = type;}

    public EventModel setType(EventType type){this.type=type;return this;}
    public EventModel setActorId(int actorId){this.actorId=actorId;return this;}
    public EventModel setEntityType(int entityType){this.entityType=entityType;return this;}
    public EventModel setEntityId(int entityId){this.entityId=entityId;return this;}
    public EventModel setEntityOwner(int entityOwner){this.entityOwner=entityOwner;return this;}
    public EventModel setExts(String key,String value){exts.put(key,value);return this;}
//    public EventModel setExts(Map<String, String> exts) {this.exts = exts;return this;}

    //get函数命名不可随意
    public EventType getType(){return type;}
    public int getActorId(){return actorId;}
    public int getEntityType(){return entityType;}
    public int getEntityId(){return entityId;}
    public int getEntityOwner(){return entityOwner;}
    public Map<String, String> getExts() {
        return exts;
    }
}
