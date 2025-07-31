package com.notify.event;

public class UserRegisteredEvent extends BaseEvent {
    private final String username;

    public UserRegisteredEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "UserRegisteredEvent{username='" + username + "'}";
    }
}
