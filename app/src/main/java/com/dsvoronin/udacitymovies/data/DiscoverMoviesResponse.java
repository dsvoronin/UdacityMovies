package com.dsvoronin.udacitymovies.data;

import java.util.List;

public class DiscoverMoviesResponse {
    public final int page;
    public final List<Movie> results;

    public DiscoverMoviesResponse(int page, List<Movie> results) {
        this.page = page;
        this.results = results;
    }
}
