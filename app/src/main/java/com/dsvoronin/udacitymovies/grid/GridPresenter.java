package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.core.Presenter;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.SortBy;

import rx.Observable;

public interface GridPresenter extends Presenter {

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

    void displayDetailActivity();

}
