package com.dsvoronin.udacitymovies.detail;

import android.util.Pair;
import android.view.MenuItem;

import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.core.PerActivity;
import com.dsvoronin.udacitymovies.data.api.MovieDBService;
import com.dsvoronin.udacitymovies.data.dto.ReviewsResponse;
import com.dsvoronin.udacitymovies.data.dto.TrailersResponse;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.Review;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.data.persist.MoviesContentProvider;
import com.dsvoronin.udacitymovies.rx.FlatIterable;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerActivity
public class DetailsModel implements Model<DetailsPresenter> {

    private final MovieDBService service;
    private final Observable<Movie> moviesSelection;
    private final StorIOContentResolver contentResolver;
    private final BehaviorSubject<Boolean> favouriteState = BehaviorSubject.create(false);
    private final CompositeSubscription subscription = new CompositeSubscription();

    private final Func1<Trailer, Boolean> youtubeOnlyFilter = new Func1<Trailer, Boolean>() {
        @Override
        public Boolean call(Trailer trailer) {
            String site = trailer.site;
            if (site.equals("YouTube")) {
                return true;
            } else {
                Timber.w("Unsupported video site detected: " + site);
                return false;
            }
        }
    };

    private final FlatIterable<Trailer> flatIterable = new FlatIterable<>();

    /**
     * emits all favourite movies on each database change
     */
    private final Observable<List<Movie>> favouritesDatabase;

    @Inject
    public DetailsModel(MovieDBService service, Observable<Movie> moviesSelection, StorIOContentResolver contentResolver) {
        this.service = service;
        this.moviesSelection = moviesSelection;
        this.contentResolver = contentResolver;

        favouritesDatabase = contentResolver.get()
                .listOfObjects(Movie.class)
                .withQuery(
                        Query.builder()
                                .uri(MoviesContentProvider.CONTENT_URI)
                                .build())
                .prepare()
                .createObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<Movie>>() {
                    @Override
                    public void call(List<Movie> movies) {
                        Timber.d("Favourites = " + movies);
                    }
                })
                .replay(1).refCount();
    }

    public Observable<List<Trailer>> trailersStream() {
        return moviesSelection.flatMap(new Func1<Movie, Observable<List<Trailer>>>() {
            @Override
            public Observable<List<Trailer>> call(Movie movie) {
                return networkSource(movie);
            }
        });
    }

    public Observable<Boolean> favouriteState() {
        return favouriteState.asObservable();
    }

    public Observable<Movie> dataStream() {
        return moviesSelection;
    }

    public Observable<List<Review>> reviewsStream() {
        return moviesSelection.flatMap(new Func1<Movie, Observable<List<Review>>>() {
            @Override
            public Observable<List<Review>> call(Movie movie) {
                return reviewsNetworkSource(movie);
            }
        });
    }

    private Observable<List<Review>> reviewsNetworkSource(Movie movie) {
        return service.getReviews(movie.id)
                .subscribeOn(Schedulers.io())
                .map(new Func1<ReviewsResponse, List<Review>>() {
                    @Override
                    public List<Review> call(ReviewsResponse reviewsResponse) {
                        return reviewsResponse.results;
                    }
                });
    }

    private Observable<List<Trailer>> networkSource(Movie movie) {
        return service.getVideos(movie.id)
                .subscribeOn(Schedulers.io())
                .map(new Func1<TrailersResponse, List<Trailer>>() {
                    @Override
                    public List<Trailer> call(TrailersResponse trailersResponse) {
                        return trailersResponse.getResults();
                    }
                })
                .flatMap(flatIterable)
                .filter(youtubeOnlyFilter)
                .toList();
    }

    @Override
    public void attachPresenter(DetailsPresenter presenter) {

        Observable<Pair<Movie, Boolean>> isMovieFavourite = moviesSelection.flatMap(new Func1<Movie, Observable<Pair<Movie, Boolean>>>() {
            @Override
            public Observable<Pair<Movie, Boolean>> call(final Movie movie) {
                return favouritesDatabase.map(new Func1<List<Movie>, Pair<Movie, Boolean>>() {
                    @Override
                    public Pair<Movie, Boolean> call(List<Movie> movies) {
                        return new Pair<>(movie, movies.contains(movie));
                    }
                });
            }
        });

        subscription.add(isMovieFavourite.first()
                .map(new Func1<Pair<Movie, Boolean>, Boolean>() {
                    @Override
                    public Boolean call(Pair<Movie, Boolean> movieBooleanPair) {
                        return movieBooleanPair.second;
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isFavourite) {
                        favouriteState.onNext(isFavourite);
                    }
                }));

        Observable<Pair<Movie, Boolean>> toggleFavourite = presenter.favouritesClicks()
                .withLatestFrom(isMovieFavourite, new Func2<MenuItem, Pair<Movie, Boolean>, Pair<Movie, Boolean>>() {
                    @Override
                    public Pair<Movie, Boolean> call(MenuItem menuItem, Pair<Movie, Boolean> movie) {
                        return new Pair<>(movie.first, !movie.second);
                    }
                });

        subscription.add(toggleFavourite.subscribe(new Action1<Pair<Movie, Boolean>>() {
            @Override
            public void call(Pair<Movie, Boolean> movie) {
                if (movie.second) {
                    contentResolver.put()
                            .object(movie.first)
                            .prepare()
                            .executeAsBlocking();
                } else {
                    contentResolver.delete()
                            .object(movie.first)
                            .prepare()
                            .executeAsBlocking();
                }
                favouriteState.onNext(movie.second);
            }
        }));
    }

    @Override
    public void detachPresenter() {
        subscription.clear();
    }
}
