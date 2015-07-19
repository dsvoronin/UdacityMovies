package com.dsvoronin.udacitymovies.grid;

import android.widget.GridView;

import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.data.DataSource;
import com.dsvoronin.udacitymovies.data.DiscoverMoviesResponse;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.SortBy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.widget.OnItemClickEvent;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MoviesGridModel implements Model {

    private final MovieDBService service;

    private final Map<SortBy, List<Movie>> inMemoryCache = new LinkedHashMap<>();

    private MoviesGridPresenter presenter;

    public MoviesGridModel(MovieDBService service) {
        this.service = service;
    }

    public void attachPresenter(MoviesGridPresenter presenter) {
        this.presenter = presenter;
    }

    public void detachPresenter() {
        this.presenter = null;
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
                        return Observable.concat(memorySource(sortBy), networkSource(sortBy))
                                .first(new Func1<List<Movie>, Boolean>() {
                                    @Override
                                    public Boolean call(List<Movie> movies) {
                                        return movies != null;
                                    }
                                });
                    }
                });
    }

    public void clearMemory() {
        Timber.d("Wiping memory...");
        inMemoryCache.clear();
    }

    private Observable<OnItemClickEvent> validSelectionsStream() {
        return presenter.movieSelectionStream()
                .filter(new Func1<OnItemClickEvent, Boolean>() {
                    @Override
                    public Boolean call(OnItemClickEvent onItemClickEvent) {
                        return onItemClickEvent.position() != GridView.INVALID_POSITION;
                    }
                })
                .share();
    }

    private Observable<List<Movie>> networkSource(final SortBy sortBy) {
        return service.getMovies(sortBy)
                .map(new Func1<DiscoverMoviesResponse, List<Movie>>() {
                    @Override
                    public List<Movie> call(DiscoverMoviesResponse discoverMoviesResponse) {
                        return discoverMoviesResponse.results;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<List<Movie>>() {
                    @Override
                    public void call(List<Movie> movies) {
                        inMemoryCache.put(sortBy, movies);
                    }
                })
                .compose(new Logger(DataSource.NETWORK));
    }

    private Observable<List<Movie>> memorySource(final SortBy sortBy) {
        return Observable.create(new Observable.OnSubscribe<List<Movie>>() {
            @Override
            public void call(Subscriber<? super List<Movie>> subscriber) {
                if (inMemoryCache.containsKey(sortBy)) {
                    subscriber.onNext(inMemoryCache.get(sortBy));
                }
                subscriber.onCompleted();
            }
        })
                .compose(new Logger(DataSource.MEMORY));
    }

    // Simple logging to let us know what each source is returning
    private static class Logger implements Observable.Transformer<List<Movie>, List<Movie>> {
        private final DataSource source;

        private Logger(DataSource source) {
            this.source = source;
        }

        @Override
        public Observable<List<Movie>> call(Observable<List<Movie>> data) {
            if (data == null) {
                Timber.d(source + " does not have any data.");
            } else {
                Timber.d(source + " has the data you are looking for!");
            }
            return data;
        }
    }
}
