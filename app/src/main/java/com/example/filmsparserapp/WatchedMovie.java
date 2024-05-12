package com.example.filmsparserapp;

public class WatchedMovie {
    private String id;
    private String title;

    public WatchedMovie(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}