package com.example.raffle_api.model;

public class Prize {
    private static long nextId = 0;
    private long id;
    private String description;

    public Prize(String description) {
        id = nextId++;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
