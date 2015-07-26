package com.dsvoronin.udacitymovies.data.dto;

import com.dsvoronin.udacitymovies.data.entities.Movie;

import java.util.List;

public class DiscoverMoviesResponse {
    public final int page;
    public final List<Movie> results;

    public DiscoverMoviesResponse(int page, List<Movie> results) {
        this.page = page;
        this.results = results;
    }
}
