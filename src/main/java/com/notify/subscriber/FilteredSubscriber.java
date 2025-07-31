package com.notify.subscriber;

import com.notify.event.Event;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteredSubscriber implements Subscriber {
    private final String id;
    private final String name;
    private final Predicate<Event> filter;
    private final Consumer<Event> action;

    public FilteredSubscriber(String id, String name, Predicate<Event> filter, Consumer<Event> action) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Subscriber ID must not be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Subscriber name must not be null or empty");
        }
        this.id = id;
        this.name = name;
        this.filter = Objects.requireNonNull(filter, "Filter must not be null");
        this.action = Objects.requireNonNull(action, "Action must not be null");
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
    public void notify(Event event) {
        action.accept(event);
    }

    @Override
    public boolean shouldNotify(Event event) {
        return filter.test(event);
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
