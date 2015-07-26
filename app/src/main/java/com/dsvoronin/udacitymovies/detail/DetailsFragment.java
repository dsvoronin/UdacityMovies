package com.dsvoronin.udacitymovies.detail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.databinding.DetailsBinding;
import com.dsvoronin.udacitymovies.databinding.TrailerRowBinding;
import com.dsvoronin.udacitymovies.grid.GridActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

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

        Glide.with(this)
                .load(movie.posterPath)
                .into(binding.detailsPoster);

        final LinearLayout trailersLayout = binding.trailers;

        subscription.add(model.dataStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnTrailersLoaded(inflater, trailersLayout)));

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

    private static class OnTrailersLoaded implements Observer<List<Trailer>> {
        private final LayoutInflater inflater;
        private final ViewGroup parent;
        private final Context context;

        private OnTrailersLoaded(LayoutInflater inflater, ViewGroup parent) {
            this.inflater = inflater;
            this.parent = parent;
            this.context = parent.getContext();
        }

        private void buildTrailerRow(LayoutInflater inflater, ViewGroup parent, final Trailer trailer) {
            TrailerRowBinding binding = TrailerRowBinding.inflate(inflater, parent, true);
            binding.setTrailer(trailer);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View v) {
                    startYoutube(trailer);
                }
            });
        }

        private void startYoutube(Trailer trailer) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailer.key));
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.key));
                context.startActivity(intent);
            }
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Can't load trailers");
            Toast.makeText(context, R.string.trailers_load_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(List<Trailer> trailers) {
            parent.removeAllViews();
            for (Trailer trailer : trailers) {
                buildTrailerRow(inflater, parent, trailer);
            }
        }
    }
}
