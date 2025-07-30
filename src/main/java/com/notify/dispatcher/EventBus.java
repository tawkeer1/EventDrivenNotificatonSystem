// Updated EventBus.java with strict type-based subscriptions (no global subscribers)
package com.notify.dispatcher;

import com.notify.event.Event;
import com.notify.subscriber.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventBus {
    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    // Map of event type -> Set of subscribers
    private final ConcurrentMap<String, Set<Subscriber>> subscribersByType = new ConcurrentHashMap<>();
    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private final List<Event> eventHistory = new CopyOnWriteArrayList<>();

    private final ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor();

    public EventBus() {
        dispatcherExecutor.submit(this::dispatchLoop);
    }

    public void subscribe(String eventType, Subscriber subscriber) {
        if (subscriber == null || subscriber.getId() == null || subscriber.getId().isBlank()) {
            logger.warn("Cannot subscribe: subscriber or subscriber ID is null/blank");
            return;
        }

        subscribersByType
                .computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet())
                .add(subscriber);

        logger.info("Registered subscriber: {} (ID: {}) for event type: {}", subscriber.getName(), subscriber.getId(), eventType);
    }

    public void unsubscribe(String eventType, String subscriberId) {
        if (subscriberId == null || subscriberId.isBlank()) {
            logger.warn("Cannot unsubscribe: subscriber ID is null or blank");
            return;
        }

        Set<Subscriber> subs = subscribersByType.get(eventType);
        if (subs != null) {
            subs.removeIf(s -> s.getId().equals(subscriberId));
            logger.info("Unsubscribed subscriber ID: {} from event type: {}", subscriberId, eventType);
        }
    }

    public void publish(Event event) {
        if (event == null) {
            logger.warn("Cannot publish: event is null");
            return;
        }
        eventQueue.offer(event);
    }

    private void dispatchLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Event event = eventQueue.take();
                eventHistory.add(event);

                logger.info("Dispatching event: {} at {}", event.getType(), event.getTimeStamp());

                Set<Subscriber> subscribers = subscribersByType.getOrDefault(event.getType(), Set.of());
                for (Subscriber subscriber : subscribers) {
                    if (subscriber.shouldNotify(event)) {
                        CompletableFuture.runAsync(() -> subscriber.notify(event));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Event dispatcher interrupted");
        } catch (Exception e) {
            logger.error("Error in event dispatch loop", e);
        }
    }

    public List<Event> getEventHistory(Predicate<Event> filter) {
        if (filter == null) return List.of();
        return eventHistory.stream().filter(filter).collect(Collectors.toList());
    }

    public List<Event> getAllEvents() {
        return List.copyOf(eventHistory);
    }

    public void shutdown() {
        dispatcherExecutor.shutdownNow();
    }
}
