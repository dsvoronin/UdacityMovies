package com.dsvoronin.udacitymovies.grid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.View;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.OnGridClickEvent;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static rx.android.view.ViewObservable.bindView;

public class MoviesGridView implements View, AdapterView.OnItemClickListener {

    private final WeakReference<Context> context;
    private final WeakReference<MoviesGridModel> model;
    private final GridView gridView;
    private final MoviesAdapter adapter;

    private final PublishSubject<OnGridClickEvent<Movie>> clickEventSubject = PublishSubject.create();

    public MoviesGridView(final Context context, MoviesGridModel model, Picasso picasso, DisplayMetrics metrics, Boolean isTablet) {
        this.context = new WeakReference<>(context);
        this.model = new WeakReference<>(model);
        this.adapter = new MoviesAdapter(context, picasso, metrics, isTablet);
        this.gridView = createView(context);

        gridView.setChoiceMode(isTablet ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);

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

    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull android.view.View view, int position, long id) {
        MoviesGridModel model = this.model.get();
        if (model != null) {
            clickEventSubject.onNext(new OnGridClickEvent<>(position, adapter.getItem(position)));
        }
    }

    private void setActivatedPosition(int position) {
        if (position != ListView.INVALID_POSITION) {
            gridView.setItemChecked(position, true);
        }
    }

    public Observable<OnGridClickEvent<Movie>> itemClicksStream() {
        return clickEventSubject.asObservable();
    }

    private void subscribeToModel() {
        MoviesGridModel model = this.model.get();
        if (model != null) {
            bindView(gridView, model.dataStream())
                    .subscribe(new Action1<List<Movie>>() {
                        @Override
                        public void call(List<Movie> movies) {
                            adapter.replace(movies);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.e(throwable, "Error while loading movies");
                            Context context = MoviesGridView.this.context.get();
                            if (context != null) {
                                showRetrySnack();
                            }
                        }
                    });
        }
    }

    private GridView createView(Context context) {
        GridView gridView = new GridView(context);
        gridView.setNumColumns(GridView.AUTO_FIT);
        gridView.setAdapter(adapter);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setOnItemClickListener(this);
        return gridView;
    }

    private void showRetrySnack() {
        Snackbar.make(gridView, R.string.loading_error, 5000)
                .setAction(R.string.loading_error_action, new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(@NonNull android.view.View v) {
                        subscribeToModel();
                    }
                })
                .show();
    }
}
