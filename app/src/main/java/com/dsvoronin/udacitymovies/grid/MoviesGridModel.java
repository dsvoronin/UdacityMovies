package com.dsvoronin.udacitymovies.grid;

import android.widget.GridView;

import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.data.DiscoverMoviesResponse;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.SortBy;

import java.util.List;

import rx.Observable;
import rx.android.widget.OnItemClickEvent;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MoviesGridModel implements Model {

    private final MovieDBService service;

    private final MoviesGridPresenter presenter;

    public MoviesGridModel(MovieDBService service, MoviesGridPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    private Observable<OnItemClickEvent> validSelectionsStream() {
        return presenter.movieSelectionStream().asObservable()
                .filter(new Func1<OnItemClickEvent, Boolean>() {
                    @Override
                    public Boolean call(OnItemClickEvent onItemClickEvent) {
                        return onItemClickEvent.position() != GridView.INVALID_POSITION;
                    }
                })
                .share();
    }

    public Observable<Movie> selectedMovieStream() {
        return validSelectionsStream()
                .map(new Func1<OnItemClickEvent, Movie>() {
                    @Override
                    public Movie call(OnItemClickEvent event) {
                        return ((MoviesAdapter) event.parent().getAdapter()).getItem(event.position());
                    }
                });
    }

    public Observable<Integer> activatedPositionStream() {
        return validSelectionsStream().map(new Func1<OnItemClickEvent, Integer>() {
            @Override
            public Integer call(OnItemClickEvent movieOnGridClickEvent) {
                return movieOnGridClickEvent.position();
            }
        });
    }

    public Observable<List<Movie>> dataStream() {
        return Observable.combineLatest(
                presenter.reloadStream(),
                presenter.sortingSelectionStream(), new Func2<Boolean, SortBy, SortBy>() {
                    @Override
                    public SortBy call(Boolean event, SortBy sortBy) {
                        return sortBy;
                    }
                })
                .flatMap(new Func1<SortBy, Observable<List<Movie>>>() {
                    @Override
                    public Observable<List<Movie>> call(SortBy sortBy) {
                        return service.getMovies(sortBy)
                                .map(new Func1<DiscoverMoviesResponse, List<Movie>>() {
                                    @Override
                                    public List<Movie> call(DiscoverMoviesResponse discoverMoviesResponse) {
                                        return discoverMoviesResponse.results;
                                    }
                                })
                                .subscribeOn(Schedulers.io());
                    }
                });
    }
}
