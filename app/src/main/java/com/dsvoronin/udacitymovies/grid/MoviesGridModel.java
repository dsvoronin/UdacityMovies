package com.dsvoronin.udacitymovies.grid;

import android.widget.ListView;

import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.data.DiscoverMoviesResponse;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.OnGridClickEvent;
import com.dsvoronin.udacitymovies.data.SortBy;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class MoviesGridModel implements Model {

    private final MovieDBService service;

    private final MoviesGridPresenter presenter;

    public static final OnGridClickEvent<Movie> INVALID_SELECTION = new OnGridClickEvent<>(ListView.INVALID_POSITION, null);

    /**
     * The current activated item position. Only used on tablets.
     */
    private BehaviorSubject<OnGridClickEvent<Movie>> activatedPosition = BehaviorSubject.create(INVALID_SELECTION);

    public MoviesGridModel(MovieDBService service, MoviesGridPresenter presenter) {
        this.service = service;
        this.presenter = presenter;

        presenter.movieSelectionStream()
                .subscribe(activatedPosition);
    }

    private Observable<OnGridClickEvent<Movie>> validSelectionsStream() {
        return activatedPosition.asObservable()
                .filter(new Func1<OnGridClickEvent<Movie>, Boolean>() {
                    @Override
                    public Boolean call(OnGridClickEvent<Movie> movieOnGridClickEvent) {
                        return movieOnGridClickEvent != INVALID_SELECTION;
                    }
                });
    }

    public Observable<Movie> selectedMovieStream() {
        return validSelectionsStream()
                .map(new Func1<OnGridClickEvent<Movie>, Movie>() {
                    @Override
                    public Movie call(OnGridClickEvent<Movie> movieOnGridClickEvent) {
                        return movieOnGridClickEvent.data;
                    }
                });
    }

    public Observable<Integer> activatedPositionStream() {
        return validSelectionsStream().map(new Func1<OnGridClickEvent<Movie>, Integer>() {
            @Override
            public Integer call(OnGridClickEvent<Movie> movieOnGridClickEvent) {
                return movieOnGridClickEvent.position;
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
