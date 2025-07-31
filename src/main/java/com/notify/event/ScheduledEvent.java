package com.notify.event;

public class ScheduledEvent extends BaseEvent {
    private final String eventType;
    public ScheduledEvent(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "ScheduledEvent{" + getType() + "}";
    }
}
