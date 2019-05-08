package com.pipixing.sns.async.handler;

import com.pipixing.sns.async.EventHandler;
import com.pipixing.sns.async.EventModel;
import com.pipixing.sns.async.EventType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UnfollowHandler implements EventHandler {
    @Override
    public void doHandler(EventModel eventModel){}
    @Override
    public List<EventType> getSupportEventTypes(){return Arrays.asList(EventType.UNFELLOW);
    }
}
