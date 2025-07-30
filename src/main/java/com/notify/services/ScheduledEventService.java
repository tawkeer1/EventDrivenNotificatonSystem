package com.notify.services;

import com.notify.dispatcher.EventBus;
import com.notify.event.ScheduledEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledEventService {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final EventBus eventBus;
    public ScheduledEventService(EventBus eventBus){
        this.eventBus = eventBus;
    }

    public void startScheduledTask(String eventType, long intervalSeconds){
        executor.scheduleAtFixedRate(() -> {
            eventBus.publish(new ScheduledEvent(eventType));
        },0, intervalSeconds, TimeUnit.SECONDS);
    }
    public void shutdown(){
        executor.shutdownNow();
    }
}
