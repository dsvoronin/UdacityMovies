package com.dsvoronin.udacitymovies.data;

import com.google.gson.annotations.SerializedName;

public class Movie {

    public final long id;

    public final String title;

    @SerializedName("poster_path")
    public final String posterPath;

    public Movie(long id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                '}';
    }
}
