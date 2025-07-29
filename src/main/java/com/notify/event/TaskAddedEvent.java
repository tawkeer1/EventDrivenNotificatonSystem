package com.notify.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TaskAddedEvent implements Event {
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String taskDetails;
    private final Priority priority;

    public TaskAddedEvent(String taskDetails, Priority priority) {
        if (taskDetails == null || taskDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Task details must not be empty.");
        }
        this.priority = Objects.requireNonNull(priority, "Priority must not be empty");
        this.taskDetails = taskDetails;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public String getType() {
        return "TASK_ADDED";
    }
}
