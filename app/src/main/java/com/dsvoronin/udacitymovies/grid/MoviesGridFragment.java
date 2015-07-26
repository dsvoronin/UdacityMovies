package com.dsvoronin.udacitymovies.grid;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.MasterCallbacks;
import com.dsvoronin.udacitymovies.core.RxActivity;
import com.dsvoronin.udacitymovies.core.RxFragment;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.data.SortBy;
import com.dsvoronin.udacitymovies.databinding.GridBinding;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static rx.android.app.AppObservable.bindSupportFragment;
import static rx.android.view.ViewObservable.bindView;

public class MoviesGridFragment extends RxFragment implements MoviesGridPresenter {

    @Inject DisplayMetrics metrics;
    @Inject Boolean isTablet;
    @Inject MovieDBService service;

    @Inject Provider<MoviesGridModel> modelProvider;

    private MoviesAdapter moviesAdapter;
    private RecyclerView gridView;
    private PublishSubject<Boolean> reloads = PublishSubject.create();

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private MasterCallbacks mCallbacks = MasterCallbacks.DUMMY_CALLBACKS;
    private MoviesGridModel model;
    private CompositeSubscription subscription = new CompositeSubscription();

    private final Action1<Throwable> moviesError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Timber.e(throwable, "Error while loading movies");
            Context context = getActivity();
            if (context != null) {
                Snackbar.make(gridView, R.string.loading_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.loading_error_action, new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(@NonNull android.view.View v) {
                                reloads.onNext(true);
                            }
                        })
                        .show();
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        DaggerMoviesGridComponent.builder()
                .appComponent(MoviesApp.get(activity).component())
                .build()
                .inject(this);

        RequestManager glide = Glide.with(this);
        moviesAdapter = new MoviesAdapter(glide);

        mCallbacks = (MasterCallbacks) activity;

        model = MoviesModelFragment.getOrCreateModel(this.getFragmentManager(),
                "grid_model",
                MoviesModelFragment.getProvider(),
                modelProvider);

        model.attachPresenter(this);

        subscription.add(bindSupportFragment(MoviesGridFragment.this, moviesAdapter.getSelectionStream())
                .subscribe(new Action1<Movie>() {
                    @Override
                    public void call(Movie movie) {
                        mCallbacks.onItemSelected(movie);
                    }
                }));
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GridBinding gridBinding = DataBindingUtil.inflate(inflater, R.layout.grid, container, false);
        gridView = gridBinding.gridView;
        int spans = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spans);
        gridView.setLayoutManager(layoutManager);
        gridView.setHasFixedSize(true);
        gridView.setAdapter(moviesAdapter);

        subscribeToModel();

        return gridBinding.getRoot();
    }

    @Override
    public void onDetach() {
        subscription.clear();
        model.detachPresenter();
        model = null;
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = MasterCallbacks.DUMMY_CALLBACKS;
        super.onDetach();
    }

    @Override
    public Observable<SortBy> sortingSelectionStream() {
        return ((RxActivity) getActivity()).onOptionsItemSelectedStream().map(new Func1<MenuItem, SortBy>() {
            @Override
            public SortBy call(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sort_by_popularity:
                        return SortBy.POPULARITY_DESC;
                    case R.id.sort_by_rating:
                        return SortBy.VOTE_AVERAGE_DESC;
                    default:
                        return SortBy.POPULARITY_DESC;
                }
            }
        });
    }

    @Override
    public Observable<Boolean> reloadStream() {
        return reloads.asObservable();
    }

    @Override
    public Observable<Movie> movieSelectionStream() {
        return moviesAdapter.getSelectionStream();
    }

    private void subscribeToModel() {
        if (model != null) {
            subscription.add(bindView(gridView, model.dataStream())
                    .doOnError(moviesError)
                    .onErrorResumeNext(Observable.<List<Movie>>empty())
                    .subscribe(moviesAdapter));
        }
    }
}
