package com.dsvoronin.udacitymovies.detail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.databinding.DetailsBinding;
import com.dsvoronin.udacitymovies.grid.GridActivity;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link GridActivity}
 * in two-pane mode (on tablets) or a {@link DetailsActivity}
 * on handsets.
 */
public class DetailsFragment extends Fragment implements DetailsPresenter, Action1<Movie> {

    private final CompositeSubscription subscription = new CompositeSubscription();

    private DetailsModel model;
    private DetailsBinding binding;

    @Inject Provider<DetailsModel> modelProvider;
    @Inject Provider<DetailsModelFragment> modelFragmentProvider;
    @Inject Observable<Movie> selectionStream;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        buildComponent(activity).inject(this);

        model = DetailsModelFragment.getOrCreateModel(getFragmentManager(),
                "details",
                modelFragmentProvider,
                modelProvider);

        model.attachPresenter(this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DetailsBinding.inflate(inflater, container, false);

        subscription.add(selectionStream.subscribe(this));

        subscription.add(model.trailersStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TrailersObserver(inflater, binding.trailers)));

        subscription.add(model.reviewsStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ReviewsObserver(inflater, binding.reviews, binding.reviewsCard)));

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

    @Override
    public void call(Movie movie) {
        binding.setMovie(movie);

        Glide.with(this)
                .load(movie.posterPath)
                .into(binding.detailsPoster);
    }

    private DetailsComponent buildComponent(Context context) {
        return DaggerDetailsComponent.builder()
                .appComponent(MoviesApp.get(context).component())
                .detailsModule(new DetailsModule())
                .build();
    }
}
