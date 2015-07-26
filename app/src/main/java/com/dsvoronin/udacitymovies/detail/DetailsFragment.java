package com.dsvoronin.udacitymovies.detail;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.databinding.DetailsBinding;
import com.dsvoronin.udacitymovies.databinding.TrailerRowBinding;
import com.dsvoronin.udacitymovies.grid.GridActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Action1;

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

    /**
     * The dummy content this fragment is presenting.
     */
    private Movie movie;
    private DetailsModel model;

    @Inject Provider<DetailsModel> modelProvider;

    @Override
    public void onAttach(Activity activity) {

        DaggerDetailsComponent.builder()
                .appComponent(MoviesApp.get(activity).component())
                .build()
                .inject(this);

        movie = getArguments().getParcelable(ARG_ITEM);

        model = DetailsModelFragment.getOrCreateModel(getFragmentManager(),
                "details_" + movie.id,
                DetailsModelFragment.getProvider(),
                modelProvider);

        model.attachPresenter(this);

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final DetailsBinding binding = DetailsBinding.inflate(inflater, container, false);

        binding.setMovie(movie);
        Glide.with(this).load(movie.posterPath)
                .into(binding.detailsPoster);

        final LinearLayout trailersLayout = binding.trailers;

        model.dataStream()
                .subscribe(new Action1<List<Trailer>>() {
                    @Override
                    public void call(List<Trailer> trailers) {
                        trailersLayout.removeAllViews();
                        for (Trailer trailer : trailers) {
                            trailersLayout.addView(buildTrailerRow(inflater, trailersLayout, trailer));
                        }
                    }
                });

        return binding.getRoot();
    }

    private View buildTrailerRow(LayoutInflater inflater, ViewGroup parent, Trailer trailer) {
        TrailerRowBinding binding = TrailerRowBinding.inflate(inflater, parent, false);
        binding.setTrailer(trailer);
        return binding.getRoot();
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
}
