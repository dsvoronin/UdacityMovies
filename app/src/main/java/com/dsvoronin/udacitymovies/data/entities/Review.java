package com.dsvoronin.udacitymovies.data.entities;

public class Review {

    public final String id;
    public final String author;
    public final String content;
    public final String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }
}
