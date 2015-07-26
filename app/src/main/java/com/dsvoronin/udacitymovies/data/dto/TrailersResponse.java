package com.dsvoronin.udacitymovies.data.dto;

import com.dsvoronin.udacitymovies.data.entities.Trailer;

import java.util.List;

public class TrailersResponse {
    private final long id;
    private final List<Trailer> results;

    public TrailersResponse(long id, List<Trailer> results) {
        this.id = id;
        this.results = results;
    }

    public long getId() {
        return id;
    }

    public List<Trailer> getResults() {
        return results;
    }
}
