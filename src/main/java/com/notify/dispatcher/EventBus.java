package com.notify.dispatcher;

import com.notify.event.Event;
import com.notify.subscriber.Subscriber;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private final List<Event> eventHistory = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    private final ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor();

    public EventBus() {
        startDispatcher();
    }

    private void startDispatcher() {
        dispatcherExecutor.submit(() -> {
            while (true) {
                try {
                    Event event = eventQueue.take(); // Blocks until event available
                    eventHistory.add(event);
                    logger.info("Dispatching event of type '{}' at {}", event.getType(), event.getTimeStamp());

                    for (Subscriber subscriber : subscribers) {
                        if (subscriber != null && subscriber.shouldNotify(event)) {
                            try {
                                logger.info("Notifying subscriber '{}' (ID '{}')", subscriber.getName(), subscriber.getId());
                                subscriber.notify(event);
                            } catch (Exception ex) {
                                logger.error("Failed to notify subscriber '{}'", subscriber.getName(), ex);
                            }
                        }
                    }

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("Dispatcher thread interrupted. Exiting...");
                    break;
                } catch (Exception e) {
                    logger.error("Unexpected error while dispatching event", e);
                }
            }
        });
    }

    public void publish(Event event) {
        if (event == null) {
            logger.warn("Cannot publish: event is null.");
            return;
        }

        try {
            eventQueue.put(event); // Will block if queue is full
            logger.info("Event of type '{}' added to queue", event.getType());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while publishing event of type '{}'", event.getType());
        }
    }

    public void subscribe(Subscriber subscriber) {
        if (subscriber == null || subscriber.getId() == null || subscriber.getId().isBlank()) {
            logger.warn("Cannot subscribe: subscriber or subscriber ID is null or blank.");
            return;
        }

        boolean alreadyExists = subscribers.stream()
                .anyMatch(s -> s.getId().equals(subscriber.getId()));

        if (alreadyExists) {
            logger.warn("Subscriber with ID '{}' already exists.", subscriber.getId());
            return;
        }

        subscribers.add(subscriber);
        logger.info("Registered new subscriber: '{}' with ID '{}'", subscriber.getName(), subscriber.getId());
    }

    public void unsubscribeById(String id) {
        if (id == null || id.isBlank()) {
            logger.warn("Cannot unsubscribe: ID is null or blank.");
            return;
        }

        boolean removed = subscribers.removeIf(s -> s.getId().equals(id));
        if (removed) {
            logger.info("Removed subscriber with ID '{}'", id);
        } else {
            logger.warn("No subscriber found with ID '{}'", id);
        }
    }

    public List<Event> getEventHistory(Predicate<Event> filter) {
        if (filter == null) {
            logger.warn("Filter predicate is null. Returning empty event list.");
            return List.of();
        }
        return eventHistory.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public List<Event> getAllEvents() {
        return List.copyOf(eventHistory);
    }

    public Optional<Subscriber> getSubscriberById(String id) {
        if (id == null || id.isBlank()) {
            logger.warn("Cannot retrieve subscriber: ID is null or blank.");
            return Optional.empty();
        }

        return subscribers.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public List<Subscriber> listSubscribers() {
        return List.copyOf(subscribers);
    }

    public void shutdown() {
        dispatcherExecutor.shutdownNow();
        logger.info("EventBus dispatcher shut down.");
    }
}
