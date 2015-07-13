package com.dsvoronin.udacitymovies.data;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MovieDBService {

    @GET("/discover/movie")
    Observable<DiscoverMoviesResponse> getMovies(@Query("sort_by") SortBy sortBy);

    @GET("/movie/{id}")
    Observable<Movie> getMovie(@Query("api_key") String apiKey,
                               @Path("id") long id);
}
