package com.dsvoronin.udacitymovies.data.dto;

import com.google.gson.annotations.SerializedName;

public class TMDBMovie {
    private final long id;

    private final String title;

    private final String overview;

    @SerializedName("poster_path")
    private final String posterPath;

    @SerializedName("release_date")
    private final String releaseDate;

    @SerializedName("vote_average")
    private final String voteAverage;

    public TMDBMovie(long id, String title, String overview, String posterPath, String releaseDate, String voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    @Override
    public String toString() {
        return "TMDBMovie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
                '}';
    }
}
