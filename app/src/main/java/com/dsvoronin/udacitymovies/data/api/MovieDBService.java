package com.dsvoronin.udacitymovies.data.api;

import com.dsvoronin.udacitymovies.data.dto.DiscoverMoviesResponse;
import com.dsvoronin.udacitymovies.data.dto.ReviewsResponse;
import com.dsvoronin.udacitymovies.data.dto.TrailersResponse;
import com.dsvoronin.udacitymovies.data.entities.SortBy;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MovieDBService {

    @GET("/discover/movie")
    Observable<DiscoverMoviesResponse> getMovies(@Query("sort_by") SortBy sortBy);

    @GET("/movie/{id}/videos")
    Observable<TrailersResponse> getVideos(@Path("id") long id);

    @GET("/movie/{id}/reviews")
    Observable<ReviewsResponse> getReviews(@Path("id") long id);
}
