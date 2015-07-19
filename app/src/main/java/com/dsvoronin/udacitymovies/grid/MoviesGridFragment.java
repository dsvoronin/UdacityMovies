package com.dsvoronin.udacitymovies.grid;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsvoronin.udacitymovies.AppModule;
import com.dsvoronin.udacitymovies.DaggerAppComponent;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.UIModule;
import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.dsvoronin.udacitymovies.core.MasterCallbacks;
import com.dsvoronin.udacitymovies.core.RxActivity;
import com.dsvoronin.udacitymovies.data.DataModule;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static rx.android.app.AppObservable.bindSupportFragment;

public class MoviesGridFragment extends Fragment {

    @Inject
    Picasso picasso;
    @Inject
    DisplayMetrics metrics;
    @Inject
    Boolean isTablet;
    @Inject
    MovieDBService service;
    @Inject
    @ImageQualifier
    String imageQualifier;
    @Inject
    @ImageEndpoint
    String imageEndpoint;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private MasterCallbacks mCallbacks = MasterCallbacks.DUMMY_CALLBACKS;
    private MoviesGridModel model;
    private MoviesGridView view;
    private MoviesGridPresenter presenter;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof MasterCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        DaggerAppComponent.builder()
                .appModule(new AppModule((MoviesApp) getActivity().getApplication()))
                .uIModule(new UIModule())
                .dataModule(new DataModule())
                .build()
                .inject(this);

        mCallbacks = (MasterCallbacks) activity;

        model = getOrCreateModel(service);
        presenter = model.getPresenter();

        subscription.add(bindSupportFragment(MoviesGridFragment.this, model.selectedMovieStream())
                .subscribe(new Action1<Movie>() {
                    @Override
                    public void call(Movie movie) {
                        mCallbacks.onItemSelected(movie);
                    }
                }));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MoviesGridView moviesGridView = new MoviesGridView(getActivity(), container, model, picasso, metrics, isTablet, imageEndpoint, imageQualifier);
        view = moviesGridView;

        subscription.add(bindSupportFragment(this, view.itemClicksStream())
                .subscribe(presenter.itemClicks));

        return moviesGridView.getView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view.onDestroy();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MoviesApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        subscription.clear();
        model = null;
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = MasterCallbacks.DUMMY_CALLBACKS;
    }

    private MoviesGridModel getOrCreateModel(MovieDBService service) {
        String MODEL_TAG = "movies_grid_model";
        MoviesModelFragment modelFragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(MODEL_TAG);
        if (fragment == null) {
            modelFragment = new MoviesModelFragment();
            modelFragment.setModel(new MoviesGridModel(service, new MoviesGridPresenter((RxActivity) getActivity())));
            fragmentManager.beginTransaction()
                    .add(modelFragment, MODEL_TAG)
                    .commit();
        } else {
            modelFragment = (MoviesModelFragment) fragment;
        }
        return modelFragment.getModel();
    }
}
