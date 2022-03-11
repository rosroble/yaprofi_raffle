package com.example.raffle_api.model;

public class Promo {
    private static int nextId = 0;
    private long id;
    private String name;
    private String description;

    public Promo(String name, String description) {
        this.id = nextId++;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDesc(String description) {
        this.description = description;
    }
}


