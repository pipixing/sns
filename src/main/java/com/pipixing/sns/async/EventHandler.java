package com.pipixing.sns.async;

import java.util.List;

public interface EventHandler {
    void doHandler(EventModel eventModel);

    //handler支持的EventType类型
    List<EventType> getSupportEventTypes();
}
