package com.dsvoronin.udacitymovies.detail;

import android.view.MenuItem;

import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.core.PerActivity;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.dto.ReviewsResponse;
import com.dsvoronin.udacitymovies.data.dto.TrailersResponse;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.Review;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.rx.FlatIterable;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerActivity
public class DetailsModel implements Model<DetailsPresenter> {

    private final MovieDBService service;
    private final Observable<Movie> moviesSelection;
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

    @Inject
    public DetailsModel(MovieDBService service, Observable<Movie> moviesSelection) {
        this.service = service;
        this.moviesSelection = moviesSelection;
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
        subscription.add(presenter.favouritesClicks().subscribe(new Action1<MenuItem>() {
            @Override
            public void call(MenuItem menuItem) {
                favouriteState.onNext(!favouriteState.getValue());
            }
        }));
    }

    @Override
    public void detachPresenter() {
        subscription.clear();
    }
}
