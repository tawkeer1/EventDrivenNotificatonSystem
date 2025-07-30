package com.notify.services;

import com.notify.dispatcher.EventBus;
import com.notify.event.TaskAddedEvent;
import com.notify.event.Priority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final EventBus eventBus;

    public TaskService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void createTask(String taskDetails, Priority priority) {
        if (taskDetails == null || taskDetails.isBlank() || priority == null) {
            logger.warn("Invalid task details or priority. Task not created.");
            return;
        }

        TaskAddedEvent event = new TaskAddedEvent(taskDetails, priority);
        logger.info("Creating and publishing new TaskAddedEvent: {}", event);
        eventBus.publish(event);
    }
}

