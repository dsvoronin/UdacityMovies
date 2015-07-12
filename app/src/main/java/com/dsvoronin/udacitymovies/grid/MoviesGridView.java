package com.dsvoronin.udacitymovies.grid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.View;
import com.dsvoronin.udacitymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Observable;
import rx.android.widget.OnItemClickEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import timber.log.Timber;

import static rx.android.view.ViewObservable.bindView;

public class MoviesGridView implements View {

    private final WeakReference<Context> context;
    private final WeakReference<MoviesGridModel> model;
    private final GridView gridView;
    private final MoviesAdapter adapter;

    public MoviesGridView(final Context context, ViewGroup parent, MoviesGridModel model, Picasso picasso, DisplayMetrics metrics, Boolean isTablet) {
        this.context = new WeakReference<>(context);
        this.model = new WeakReference<>(model);

        int imageWidth = determineImageWidth(isTablet, metrics);

        this.adapter = new MoviesAdapter(context, picasso, imageWidth);
        this.gridView = createView(context, parent);

        gridView.setAdapter(adapter);
        gridView.setChoiceMode(isTablet ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);

        gridView.setColumnWidth(imageWidth);

        subscribeToModel();

        bindView(gridView, model.activatedPositionStream())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        setActivatedPosition(position);
                    }
                });

    }

    public android.view.View getView() {
        return gridView;
    }

    private void setActivatedPosition(int position) {
        if (position != ListView.INVALID_POSITION) {
            gridView.setItemChecked(position, true);
        }
    }

    public Observable<OnItemClickEvent> itemClicksStream() {
        return WidgetObservable.itemClicks(gridView);
    }

    private void subscribeToModel() {
        MoviesGridModel model = this.model.get();
        if (model != null) {
            bindView(gridView, model.dataStream())
                    .doOnError(moviesError)
                    .onErrorResumeNext(Observable.<List<Movie>>empty())
                    .subscribe(adapter);
        }
    }

    private GridView createView(Context context, ViewGroup parent) {
        return (GridView) LayoutInflater.from(context).inflate(R.layout.grid, parent, false);
    }

    private final Action1<Throwable> moviesError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Timber.e(throwable, "Error while loading movies");
            Context context = MoviesGridView.this.context.get();
            if (context != null) {
                Snackbar.make(gridView, R.string.loading_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.loading_error_action, new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(@NonNull android.view.View v) {
                                subscribeToModel();
                            }
                        })
                        .show();
            }
        }
    };

    private int determineImageWidth(boolean isTablet, DisplayMetrics metrics) {
        if (isTablet) {
            return metrics.widthPixels / 4;
        } else {
            return metrics.widthPixels / 2;
        }
    }
}
