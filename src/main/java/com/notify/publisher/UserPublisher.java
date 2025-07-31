package com.notify.publisher;

import java.util.UUID;

public class UserPublisher implements Publisher {
    private final String id;
    private final String name;

    public UserPublisher(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public PublisherType getType() { return PublisherType.USER; }
}

