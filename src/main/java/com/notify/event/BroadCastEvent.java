package com.notify.event;

public class BroadCastEvent extends BaseEvent {
    private final String message;

    public BroadCastEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Broadcast: " + message;
    }
}
