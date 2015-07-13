package com.dsvoronin.udacitymovies.detail;

import com.dsvoronin.udacitymovies.data.MovieDBService;

public class MovieDetailModel {

    private final MovieDBService service;

    public MovieDetailModel(MovieDBService service) {
        this.service = service;
    }

}
