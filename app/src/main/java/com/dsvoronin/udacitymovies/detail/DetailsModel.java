package com.dsvoronin.udacitymovies.detail;

import com.dsvoronin.udacitymovies.core.Model;
import com.dsvoronin.udacitymovies.core.PerActivity;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.dto.TrailersResponse;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.rx.FlatIterable;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@PerActivity
public class DetailsModel implements Model<DetailsPresenter> {

    private final MovieDBService service;
    private DetailsPresenter presenter;

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
    public DetailsModel(MovieDBService service) {
        this.service = service;
    }

    public Observable<List<Trailer>> dataStream() {
        return presenter.idStream().flatMap(new Func1<Long, Observable<List<Trailer>>>() {
            @Override
            public Observable<List<Trailer>> call(Long aLong) {
                return networkSource(aLong);
            }
        });
    }

    private Observable<List<Trailer>> networkSource(Long id) {
        return service.getVideos(id)
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
        this.presenter = presenter;
    }

    @Override
    public void detachPresenter() {
        this.presenter = null;
    }
}
