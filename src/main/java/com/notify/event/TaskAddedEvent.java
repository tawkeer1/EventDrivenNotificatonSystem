package com.notify.event;

import com.notify.event.Priority;

public class TaskAddedEvent extends BaseEvent {
    private final String taskDetails;
    private final Priority priority;

    public TaskAddedEvent(String taskDetails, Priority priority) {
        this.taskDetails = taskDetails;
        this.priority = priority;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public String getType() {
        return "TASKADDED";
    }

    @Override
    public String toString() {
        return "TaskAddedEvent{details='" + taskDetails + "', priority=" + priority + "}";
    }
}
