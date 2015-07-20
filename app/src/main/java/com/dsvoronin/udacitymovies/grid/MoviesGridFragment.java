package com.dsvoronin.udacitymovies.grid;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.dsvoronin.udacitymovies.core.MasterCallbacks;
import com.dsvoronin.udacitymovies.core.RxFragment;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static rx.android.app.AppObservable.bindSupportFragment;

public class MoviesGridFragment extends RxFragment<MoviesGridView> {

    @Inject Picasso picasso;
    @Inject DisplayMetrics metrics;
    @Inject Boolean isTablet;
    @Inject MovieDBService service;

    @Inject
    @ImageQualifier
    String imageQualifier;

    @Inject
    @ImageEndpoint
    String imageEndpoint;

    @Inject
    Provider<MoviesGridModel> modelProvider;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private MasterCallbacks mCallbacks = MasterCallbacks.DUMMY_CALLBACKS;
    private MoviesGridModel model;
    private MoviesGridPresenter presenter;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public void onAttach(Activity activity) {
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof MasterCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        DaggerMoviesGridComponent.builder()
                .appComponent(MoviesApp.get(activity).component())
                .build()
                .inject(this);

        mCallbacks = (MasterCallbacks) activity;

        model = MoviesModelFragment.getOrCreateModel(this.getFragmentManager(),
                "grid_model",
                MoviesModelFragment.getProvider(),
                modelProvider);

        presenter = new MoviesGridPresenter();
        model.attachPresenter(presenter);
        presenter.attach(this);

        subscription.add(bindSupportFragment(MoviesGridFragment.this, model.selectedMovieStream())
                .subscribe(new Action1<Movie>() {
                    @Override
                    public void call(Movie movie) {
                        mCallbacks.onItemSelected(movie);
                    }
                }));
        super.onAttach(activity);
    }

    @Override
    protected MoviesGridView createView(@Nullable ViewGroup container) {
        return new MoviesGridView(
                getActivity(),
                container,
                model,
                picasso,
                metrics,
                isTablet,
                imageEndpoint,
                imageQualifier);
    }

    @Override
    public void onDetach() {
        presenter.detach();
        subscription.clear();
        model.detachPresenter();
        model = null;
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = MasterCallbacks.DUMMY_CALLBACKS;
        super.onDetach();
    }
}
