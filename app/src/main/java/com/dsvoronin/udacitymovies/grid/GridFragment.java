package com.dsvoronin.udacitymovies.grid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.dsvoronin.udacitymovies.detail.DetailsActivity;
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
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class GridFragment extends RxFragment implements GridPresenter {

    @Inject Observer<Movie> selectionSubject;
    @Inject Provider<GridModel> modelProvider;
    @Inject Provider<GridModelFragment> modelFragmentProvider;

    private GridAdapter gridAdapter;
    private RecyclerView gridView;
    private PublishSubject<Boolean> reloads = PublishSubject.create();
    private GridModel model;

    private final CompositeSubscription subscription = new CompositeSubscription();

    private final Action1<Throwable> moviesError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Timber.e(throwable, "Error while loading movies");
            Snackbar.make(gridView, R.string.loading_error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.loading_error_action, new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(@NonNull android.view.View v) {
                            reloads.onNext(true);
                        }
                    })
                    .show();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((GridActivity) activity).component().inject(this);

        RequestManager glide = Glide.with(this);
        gridAdapter = new GridAdapter(glide);

        model = GridModelFragment.getOrCreateModel(this.getFragmentManager(),
                "grid_model",
                modelFragmentProvider,
                modelProvider);

        model.attachPresenter(this);

        subscription.add(gridAdapter.getSelectionStream()
                .subscribe(selectionSubject));
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

        subscription.add(model.dataStream()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(moviesError)
                .onErrorResumeNext(Observable.<List<Movie>>empty())
                .subscribe(gridAdapter));

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
        return ((RxActivity) getActivity()).onOptionsItemSelectedStream()
                .map(new Func1<MenuItem, Integer>() {
                    @Override
                    public Integer call(MenuItem menuItem) {
                        return menuItem.getItemId();
                    }
                })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer itemId) {
                        return itemId != R.id.favourites;
                    }
                })
                .map(new Func1<Integer, SortBy>() {
                    @Override
                    public SortBy call(Integer itemId) {
                        switch (itemId) {
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

    /**
     * emits only options menu Favourite clicks
     */
    @Override
    public Observable<Boolean> favouritesSelectionStream() {
        return ((RxActivity) getActivity()).onOptionsItemSelectedStream()
                .map(new Func1<MenuItem, Boolean>() {
                    @Override
                    public Boolean call(MenuItem menuItem) {
                        return menuItem.getItemId() == R.id.favourites;
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

    @Override
    public void displayDetailActivity() {
        Context context = getActivity();
        context.startActivity(new Intent(context, DetailsActivity.class));
    }

}
