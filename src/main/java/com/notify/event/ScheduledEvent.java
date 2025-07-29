package com.notify.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScheduledEvent implements Event{
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String eventType;

    public ScheduledEvent(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getType() {
        return this.eventType;
    }
}
