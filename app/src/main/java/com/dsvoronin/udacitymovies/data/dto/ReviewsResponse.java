package com.dsvoronin.udacitymovies.data.dto;

import com.dsvoronin.udacitymovies.data.entities.Review;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsResponse {
    public final long id;
    public final int page;
    public final List<Review> results;
    @SerializedName("total_pages") public final int totalPages;
    @SerializedName("total_results") public final int totalResults;

    public ReviewsResponse(long id, int page, List<Review> results, int totalPages, int totalResults) {
        this.id = id;
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }
}
