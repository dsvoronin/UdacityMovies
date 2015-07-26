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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.SortBy;
import com.dsvoronin.udacitymovies.databinding.GridBinding;
import com.dsvoronin.udacitymovies.rx.RxActivity;
import com.dsvoronin.udacitymovies.rx.RxFragment;

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

public class GridFragment extends RxFragment implements GridPresenter {

    @Inject PublishSubject<Movie> selectionSubject;
    @Inject Provider<GridModel> modelProvider;
    @Inject Provider<GridModelFragment> modelFragmentProvider;

    private GridAdapter gridAdapter;
    private RecyclerView gridView;
    private PublishSubject<Boolean> reloads = PublishSubject.create();
    private GridModel model;
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
        ((GridActivity) activity).component().inject(this);

        RequestManager glide = Glide.with(this);
        gridAdapter = new GridAdapter(glide);

        model = GridModelFragment.getOrCreateModel(this.getFragmentManager(),
                "grid_model",
                modelFragmentProvider,
                modelProvider);

        model.attachPresenter(this);

        subscription.add(bindSupportFragment(GridFragment.this, gridAdapter.getSelectionStream())
                .subscribe(selectionSubject));
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
        gridView.setAdapter(gridAdapter);

        subscribeToModel();

        return gridBinding.getRoot();
    }

    @Override
    public void onDetach() {
        subscription.clear();
        model.detachPresenter();
        model = null;
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
        return gridAdapter.getSelectionStream();
    }

    private void subscribeToModel() {
        if (model != null) {
            subscription.add(bindView(gridView, model.dataStream())
                    .doOnError(moviesError)
                    .onErrorResumeNext(Observable.<List<Movie>>empty())
                    .subscribe(gridAdapter));
        }
    }
}
