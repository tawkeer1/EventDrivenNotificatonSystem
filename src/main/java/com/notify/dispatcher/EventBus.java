package com.notify.dispatcher;

import com.notify.event.BaseEvent;
import com.notify.event.BroadCastEvent;
import com.notify.event.Event;
import com.notify.publisher.Publisher;
import com.notify.subscriber.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventBus {
    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    private final ConcurrentMap<String, Set<Subscriber>> subscribersByType = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> subscriberToEventTypes = new ConcurrentHashMap<>();
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

        subscriberToEventTypes
                .computeIfAbsent(subscriber.getId(), k -> ConcurrentHashMap.newKeySet())
                .add(eventType);

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
        }

        Set<String> types = subscriberToEventTypes.get(subscriberId);
        if (types != null) {
            types.remove(eventType);
            if (types.isEmpty()) subscriberToEventTypes.remove(subscriberId);
        }

        logger.info("Unsubscribed subscriber ID: {} from event type: {}", subscriberId, eventType);
    }

    public void publish(Event event, Publisher publisher) {
        if (event == null) {
            logger.warn("Cannot publish: event is null");
            return;
        }

        if (event instanceof BaseEvent base) {
            base.setPublisher(publisher);
        }

        logger.info("Event of type '{}' published by [{} - {}]", event.getType(), publisher.getType(), publisher.getName());
        eventQueue.offer(event);
    }

    private void dispatchLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Event event = eventQueue.take();
                eventHistory.add(event);

                logger.info("Dispatching event: {} at {}", event.getType(), event.getTimeStamp());

                Set<Subscriber> targets;
                if (event instanceof BroadCastEvent) {
                    // send to all subscribers
                    targets = subscribersByType.values().stream()
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());
                } else {
                    targets = subscribersByType.getOrDefault(event.getType(), Set.of());
                }

                for (Subscriber subscriber : targets) {
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

    public Map<String, List<Subscriber>> listSubscribersByEventType() {
        return subscribersByType.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())
                ));
    }

    public Map<String, Set<String>> getSubscriberEventMap() {
        return Collections.unmodifiableMap(subscriberToEventTypes);
    }

    public List<Subscriber> listSubscribers() {
        return subscribersByType.values().stream()
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void shutdown() {
        dispatcherExecutor.shutdownNow();
    }
}
