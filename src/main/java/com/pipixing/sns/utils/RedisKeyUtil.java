package com.pipixing.sns.utils;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BTZ_EVENTQUEUE = "EVENTQUEUE";
    private static String BTZ_FOLLOWER = "FOLLOWER";
    private static String BTZ_FOLLOWEE = "FOLLOWEE";

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
        return  BIZ_LIKE + SPLIT + String.valueOf(entityType) +SPLIT + String.valueOf(entityId);
    }

    //某个用户对某类实体的关注key
    public static String getFolloweeKey(int userId,int entityId){
        return  BIZ_DISLIKE + SPLIT + String.valueOf(userId) +SPLIT + String.valueOf(entityId);
    }
}
