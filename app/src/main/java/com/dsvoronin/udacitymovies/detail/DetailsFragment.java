package com.dsvoronin.udacitymovies.detail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.databinding.DetailsBinding;
import com.dsvoronin.udacitymovies.grid.GridActivity;
import com.dsvoronin.udacitymovies.rx.RxActivity;
import com.dsvoronin.udacitymovies.rx.RxFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static rx.Observable.combineLatest;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link GridActivity}
 * in two-pane mode (on tablets) or a {@link DetailsActivity}
 * on handsets.
 */
public class DetailsFragment extends RxFragment implements DetailsPresenter, Observer<Movie> {

    private final CompositeSubscription subscription = new CompositeSubscription();
    @Inject Provider<DetailsModel> modelProvider;
    @Inject Provider<DetailsModelFragment> modelFragmentProvider;
    private DetailsModel model;
    private DetailsBinding binding;
    private BehaviorSubject<MenuItem> favouriteItem = BehaviorSubject.create();
    private BehaviorSubject<MenuItem> shareItem = BehaviorSubject.create();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        buildComponent(activity).inject(this);

        model = DetailsModelFragment.getOrCreateModel(getFragmentManager(),
                "details",
                modelFragmentProvider,
                modelProvider);

        model.attachPresenter(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details, menu);
        favouriteItem.onNext(menu.findItem(R.id.favourite));
        shareItem.onNext(menu.findItem(R.id.share));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DetailsBinding.inflate(inflater, container, false);

        subscription.add(model.dataStream().subscribe(this));

        subscription.add(model.trailersStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TrailersObserver(inflater, binding.trailers)));

        subscription.add(model.reviewsStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ReviewsObserver(inflater, binding.reviews, binding.reviewsCard)));

        subscription.add(combineLatest(model.favouriteState()
                        .observeOn(AndroidSchedulers.mainThread()), favouriteItem, new Func2<Boolean, MenuItem, Pair<Boolean, MenuItem>>() {
                    @Override
                    public Pair<Boolean, MenuItem> call(Boolean aBoolean, MenuItem menuItem) {
                        return new Pair<>(aBoolean, menuItem);
                    }
                }).subscribe(new Action1<Pair<Boolean, MenuItem>>() {
                    @Override
                    public void call(Pair<Boolean, MenuItem> pair) {
                        Timber.d("Favourite: " + pair.first);
                        pair.second.setIcon(pair.first ? R.drawable.ic_action_favorite : R.drawable.ic_action_favorite_border);
                        getActivity().supportInvalidateOptionsMenu();
                    }
                })
        );

        Observable<Boolean> hasAnyTrailersStream = model.trailersStream()
                .map(new Func1<List<Trailer>, Boolean>() {
                    @Override
                    public Boolean call(List<Trailer> trailers) {
                        return !trailers.isEmpty();
                    }
                })
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        subscription.add(combineLatest(hasAnyTrailersStream, shareItem, new Func2<Boolean, MenuItem, Pair<Boolean, MenuItem>>() {
            @Override
            public Pair<Boolean, MenuItem> call(Boolean aBoolean, MenuItem menuItem) {
                return new Pair<>(aBoolean, menuItem);
            }
        })
                .subscribe(new Action1<Pair<Boolean, MenuItem>>() {
                    @Override
                    public void call(Pair<Boolean, MenuItem> pair) {
                        pair.second.setVisible(pair.first);
                        getActivity().supportInvalidateOptionsMenu();
                    }
                }));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        subscription.clear();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        model.detachPresenter();
    }

    private DetailsComponent buildComponent(Context context) {
        return DaggerDetailsComponent.builder()
                .appComponent(MoviesApp.get(context).component())
                .detailsModule(new DetailsModule())
                .build();
    }

    @Override
    public Observable<MenuItem> favouritesClicks() {
        return ((RxActivity) getActivity()).onOptionsItemSelectedStream()
                .filter(new Func1<MenuItem, Boolean>() {
                    @Override
                    public Boolean call(MenuItem menuItem) {
                        return menuItem.getItemId() == R.id.favourite;
                    }
                });
    }

    @Override
    public Observable<MenuItem> shareClicks() {
        return ((RxActivity) getActivity()).onOptionsItemSelectedStream()
                .filter(new Func1<MenuItem, Boolean>() {
                    @Override
                    public Boolean call(MenuItem menuItem) {
                        return menuItem.getItemId() == R.id.share;
                    }
                });
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Can't load movie");
        Toast.makeText(getActivity(), R.string.trailers_load_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(Movie movie) {
        binding.setMovie(movie);

        Glide.with(this)
                .load(movie.posterPath.toString())
                .into(binding.detailsPoster);
    }
}
