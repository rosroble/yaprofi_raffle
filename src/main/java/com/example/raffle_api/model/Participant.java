package com.example.raffle_api.model;

public class Participant {

    private static long nextId = 0;
    private long id;
    private String name;

    public Participant(String name) {
        id = nextId++;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
