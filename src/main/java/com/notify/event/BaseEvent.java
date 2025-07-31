package com.notify.event;

import com.notify.publisher.Publisher;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEvent implements Event {
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime timestamp = LocalDateTime.now();
    private Publisher publisher;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return timestamp;
    }

    @Override
    public String getType() {
        // Automatically use simple class name as event type
        return this.getClass().getSimpleName().toUpperCase();
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + getType() + '\'' +
                ", timestamp=" + timestamp +
                ", id=" + id +
                ", publisher=" + (publisher != null ? publisher.getName() : "N/A") +
                '}';
    }
}
