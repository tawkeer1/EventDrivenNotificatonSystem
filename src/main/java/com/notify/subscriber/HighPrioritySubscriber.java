package com.notify.subscriber;

import com.notify.event.Event;

import com.notify.event.Priority;
import com.notify.event.TaskAddedEvent;
import com.notify.utilities.BaseLogger;

import java.time.LocalDateTime;

public class HighPrioritySubscriber extends BaseLogger implements Subscriber {

    @Override
    public void notify(Event event){
        logger.info( "[High priority] Event: " + event);
    }

    public boolean shouldNotify(Event event){
        return event instanceof TaskAddedEvent &&
                ((TaskAddedEvent) event).getPriority() == Priority.HIGH;
    }

}


