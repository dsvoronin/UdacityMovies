package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.core.DeviceClass;
import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.core.PerActivity;
import com.dsvoronin.udacitymovies.data.DataSource;
import com.dsvoronin.udacitymovies.data.DataSourceLogger;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.dto.DiscoverMoviesResponse;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.SortBy;
import com.dsvoronin.udacitymovies.rx.FlatIterable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerActivity
public class GridModel implements Model<GridPresenter> {

    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private final SimpleDateFormat tmdbFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final MovieDBService service;

    private final Map<SortBy, List<Movie>> inMemoryCache = new LinkedHashMap<>();

    private GridPresenter presenter;

    private final String imageEndpoint;
    private final String imageQualifier;
    private final Observable<Movie> movieSelection;
    private final DeviceClass deviceClass;

    private final FlatIterable<Movie> flatIterable = new FlatIterable<>();
    private final CompositeSubscription subscription = new CompositeSubscription();

    @Inject
    public GridModel(MovieDBService service, @ImageEndpoint String imageEndpoint, @ImageQualifier String imageQualifier, Observable<Movie> movieSelection, DeviceClass deviceClass) {
        this.service = service;
        this.imageEndpoint = imageEndpoint;
        this.imageQualifier = imageQualifier;
        this.movieSelection = movieSelection;
        this.deviceClass = deviceClass;
    }

    public void attachPresenter(final GridPresenter presenter) {
        this.presenter = presenter;

        subscription.add(movieSelection
                .skip(150, TimeUnit.MILLISECONDS) //todo something better :P
                .subscribe(new Action1<Movie>() {
                    @Override
                    public void call(Movie movie) {
                        if (deviceClass == DeviceClass.PHONE) presenter.displayDetailActivity();
                    }
                }));
    }

    public void detachPresenter() {
        subscription.clear();
        this.presenter = null;
    }

    public Observable<List<Movie>> dataStream() {
        return Observable.combineLatest(
                presenter.reloadStream().startWith(true),
                presenter.sortingSelectionStream().startWith(SortBy.POPULARITY_DESC), new Func2<Boolean, SortBy, SortBy>() {
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

    private Observable<List<Movie>> networkSource(final SortBy sortBy) {
        return service.getMovies(sortBy)
                .map(new Func1<DiscoverMoviesResponse, List<Movie>>() {
                    @Override
                    public List<Movie> call(DiscoverMoviesResponse discoverMoviesResponse) {
                        return discoverMoviesResponse.results;
                    }
                })
                .flatMap(flatIterable)
                .map(new Func1<Movie, Movie>() {
                    @Override
                    public Movie call(Movie movie) {
                        try {
                            return new Movie(
                                    movie.id,
                                    movie.title,
                                    movie.overview,
                                    imageEndpoint + imageQualifier + movie.posterPath,
                                    yearFormat.format(tmdbFormat.parse(movie.releaseDate)),
                                    movie.voteAverage);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<List<Movie>>() {
                    @Override
                    public void call(List<Movie> movies) {
                        inMemoryCache.put(sortBy, movies);
                    }
                })
                .compose(new DataSourceLogger<Movie>(DataSource.NETWORK));
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
                .compose(new DataSourceLogger<Movie>(DataSource.MEMORY));
    }
}
