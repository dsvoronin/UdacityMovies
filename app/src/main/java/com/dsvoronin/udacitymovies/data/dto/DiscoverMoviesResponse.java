package com.dsvoronin.udacitymovies.data.dto;

import java.util.List;

public class DiscoverMoviesResponse {
    public final int page;
    public final List<TMDBMovie> results;

    public DiscoverMoviesResponse(int page, List<TMDBMovie> results) {
        this.page = page;
        this.results = results;
    }
}
