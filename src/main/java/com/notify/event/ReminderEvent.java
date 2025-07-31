package com.notify.event;

public class ReminderEvent extends BaseEvent {
    private final String message;

    public ReminderEvent(String message) {
        this.message = message;
    }

    @Override
    public String getType() {
        return "REMINDEREVENT";
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ReminderEvent{message='" + message + "'}";
    }
}
