package com.notify.services;

import com.notify.event.Event;
import com.notify.subscriber.FilteredSubscriber;
import com.notify.subscriber.Subscriber;

import java.util.function.Consumer;

public class SubscriptionFactory {
    public static Subscriber taskOnly(String id, String name, Consumer<Event> handler){
        return new FilteredSubscriber(id, name, e -> e.getTimeStamp().equals("TASK_ADDED"), handler);
    }

    public static Subscriber everything(String id, String name, Consumer<Event> handler){
        return new FilteredSubscriber(id, name, e -> true, handler);
    }

    public static Subscriber excludeType(String id, String name, String typeToIgnore, Consumer<Event> handler){
        return new FilteredSubscriber(id, name, e -> !e.getType().equals(typeToIgnore), handler);
    }
}
