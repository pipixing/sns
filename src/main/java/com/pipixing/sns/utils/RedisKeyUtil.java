package com.pipixing.sns.utils;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BTZ_EVENTQUEUE = "EVENTQUEUE";
    private static String BTZ_FOLLOWER = "FOLLOWER";
    private static String BTZ_FOLLOWEE = "FOLLOWEE";
    private static String BTZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(int entityType,int entityId){
        return  BIZ_LIKE + SPLIT + String.valueOf(entityType) +SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType,int entityId){
        return  BIZ_DISLIKE + SPLIT + String.valueOf(entityType) +SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey(){
        return BTZ_EVENTQUEUE;
    }

    //某个实体的粉丝的key
    public static String getFollowerKey(int entityType,int entityId){
        return  BTZ_FOLLOWER + SPLIT + String.valueOf(entityType) +SPLIT + String.valueOf(entityId);
    }

    //某个用户对某类实体的关注key
    public static String getFolloweeKey(int userId,int entityType){
        return  BTZ_FOLLOWEE + SPLIT + String.valueOf(userId) +SPLIT + String.valueOf(entityType);
    }

    //Timeline KEY
    public static String getTimelineKey(int follower) {
        return  BTZ_TIMELINE + SPLIT + String.valueOf(follower);
    }
}
