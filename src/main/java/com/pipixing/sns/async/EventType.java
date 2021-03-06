package com.pipixing.sns.async;

public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    TIMELINE(6);

    private int value;
    EventType(int value){this.value = value;}
    public int getValue(){return value;}
}
