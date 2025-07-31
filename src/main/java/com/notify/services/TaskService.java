package com.notify.services;

import com.notify.dispatcher.EventBus;
import com.notify.event.TaskAddedEvent;
import com.notify.event.Priority;
import com.notify.publisher.Publisher;

public class TaskService {
    private final EventBus eventBus;

    public TaskService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void createTask(String taskDetails, Priority priority, Publisher publisher) {
        TaskAddedEvent event = new TaskAddedEvent(taskDetails, priority);
        eventBus.publish(event, publisher);
    }
}
