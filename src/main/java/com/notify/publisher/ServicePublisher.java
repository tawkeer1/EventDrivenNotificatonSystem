package com.notify.publisher;

public class ServicePublisher implements Publisher {
    private final String name;

    public ServicePublisher(String name) {
        this.name = name;
    }

    public String getId() { return name.toLowerCase().replace(" ", "_") + "_svc"; }
    public String getName() { return name; }
    public PublisherType getType() { return PublisherType.SERVICE; }
}

