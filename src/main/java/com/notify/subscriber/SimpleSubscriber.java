package com.notify.subscriber;

import com.notify.event.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SimpleSubscriber implements Subscriber {
    private final Logger logger = LoggerFactory.getLogger(SimpleSubscriber.class);
    private final String id;
    private final String name;

    public SimpleSubscriber(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void notify(Event event) {
        logger.info("[" + name + "] received event: " + event.getType() + " at " + event.getTimeStamp());
    }

    @Override
    public boolean shouldNotify(Event event) {
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilteredSubscriber that = (FilteredSubscriber) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

