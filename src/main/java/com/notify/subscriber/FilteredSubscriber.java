package com.notify.subscriber;

import com.notify.event.Event;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteredSubscriber implements Subscriber{
    private final String id;
    private final String name;
    private final Predicate<Event> filter;
    private final Consumer<Event> action;

    public FilteredSubscriber(String id, String name, Predicate<Event> filter, Consumer<Event> action) {
        this.id = id;
        this.name = id;
        this.filter = filter;
        this.action = action;
    }

    @Override
    public String getId(){
        return id;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void notify(Event event){
        action.accept(event);
    }

    @Override
    public boolean shouldNotify(Event event){
        return filter.test(event);
    }
}
