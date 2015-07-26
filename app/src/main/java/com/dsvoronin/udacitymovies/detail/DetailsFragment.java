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
import rx.subscriptions.CompositeSubscription;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link GridActivity}
 * in two-pane mode (on tablets) or a {@link DetailsActivity}
 * on handsets.
 */
public class DetailsFragment extends Fragment implements DetailsPresenter {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "item";

    private Movie movie;
    private DetailsModel model;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Inject Provider<DetailsModel> modelProvider;
    @Inject Provider<DetailsModelFragment> modelFragmentProvider;

    @Override
    public void onAttach(Activity activity) {
        buildComponent(activity).inject(this);

        movie = getArguments().getParcelable(ARG_ITEM);

        model = DetailsModelFragment.getOrCreateModel(getFragmentManager(),
                "details_" + movie.id,
                modelFragmentProvider,
                modelProvider);

        model.attachPresenter(this);

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final DetailsBinding binding = DetailsBinding.inflate(inflater, container, false);

        binding.setMovie(movie);

        Glide.with(this)
                .load(movie.posterPath)
                .into(binding.detailsPoster);

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
        super.onDestroyView();
        subscription.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        model.detachPresenter();
    }

    @Override
    public Observable<Long> idStream() {
        return Observable.just(movie.id);
    }

    private DetailsComponent buildComponent(Context context) {
        return DaggerDetailsComponent.builder()
                .detailsModule(new DetailsModule())
                .appComponent(MoviesApp.get(context).component())
                .build();
    }

}
