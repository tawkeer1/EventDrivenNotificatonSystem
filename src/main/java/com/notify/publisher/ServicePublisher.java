package com.notify.publisher;

import java.util.UUID;

public class ServicePublisher implements Publisher {
    private final String id;
    private final String name;

    public ServicePublisher(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public PublisherType getType() { return PublisherType.SERVICE; }
}

