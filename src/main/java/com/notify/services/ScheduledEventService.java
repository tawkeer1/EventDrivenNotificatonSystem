package com.notify.services;

import com.notify.dispatcher.EventBus;
import com.notify.event.ScheduledEvent;
import com.notify.publisher.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ScheduledEventService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledEventService.class);

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2); // Allows multiple schedules
    private final EventBus eventBus;

    public ScheduledEventService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void startScheduledTask(String eventType, long intervalSeconds, Publisher publisher) {
        if (eventType == null || eventType.isBlank() || intervalSeconds <= 0) {
            logger.warn("Invalid input to startScheduledTask: eventType='{}', interval={}", eventType, intervalSeconds);
            return;
        }

        executor.scheduleAtFixedRate(() -> {
            ScheduledEvent event = new ScheduledEvent(eventType);
            event.setPublisher(publisher);
            eventBus.publish(event,publisher);
            logger.info("Scheduled event '{}' published by '{}'", eventType, publisher != null ? publisher.getName() : "System");
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void shutdown() {
        executor.shutdownNow();
        logger.info("ScheduledEventService executor shutdown.");
    }
}
