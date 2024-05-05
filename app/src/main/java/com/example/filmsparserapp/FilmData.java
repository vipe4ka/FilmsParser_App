package com.example.filmsparserapp;

import java.util.List;

public class FilmData {
    private String posterUrl;
    private String title;
    private String rating;
    private String genre;
    private String description;
    public FilmData() {

    }

    public String getPosterUrl() {
        return posterUrl;
    }
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) { this.title = title; }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getGenre() { return genre; }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
