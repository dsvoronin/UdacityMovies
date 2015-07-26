package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.SortBy;

import rx.Observable;

public interface MoviesGridPresenter {

    /**
     * Emits user selections of sorting options
     */
    Observable<SortBy> sortingSelectionStream();

    /**
     * Emits user-initiated reloads, such as SwipeToRefresh
     */
    Observable<Boolean> reloadStream();

    /**
     * Emits movie selections
     */
    Observable<Movie> movieSelectionStream();

}
