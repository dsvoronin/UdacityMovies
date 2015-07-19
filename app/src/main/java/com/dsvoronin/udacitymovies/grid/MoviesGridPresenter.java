package com.dsvoronin.udacitymovies.grid;

import android.view.MenuItem;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.RxActivity;
import com.dsvoronin.udacitymovies.data.SortBy;

import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.widget.OnItemClickEvent;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class MoviesGridPresenter {

    private final RxActivity rxActivity;

    PublishSubject<OnClickEvent> reloads = PublishSubject.create();
    PublishSubject<OnItemClickEvent> itemClicks = PublishSubject.create();

    public MoviesGridPresenter(RxActivity rxActivity) {
        this.rxActivity = rxActivity;
    }

    /**
     * Emits user selections of sorting options
     */
    public Observable<SortBy> sortingSelectionStream() {
        return rxActivity.onOptionsItemSelectedStream()
                .map(new Func1<MenuItem, SortBy>() {
                    @Override
                    public SortBy call(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sort_by_popularity:
                                return SortBy.POPULARITY_DESC;
                            case R.id.sort_by_rating:
                                return SortBy.VOTE_AVERAGE_DESC;
                            default:
                                throw new IllegalArgumentException("Unsupported menu item: " + menuItem.getTitle());
                        }
                    }
                })
                .startWith(SortBy.POPULARITY_DESC);
    }

    /**
     * Emits user-initiated reloads, such as SwipeToRefresh
     */
    public Observable<Boolean> reloadStream() {
        return reloads.asObservable().map(new Func1<OnClickEvent, Boolean>() {
            @Override
            public Boolean call(OnClickEvent event) {
                return true;
            }
        })
                .startWith(true);
    }

    /**
     * Emits movie selections
     */
    public Observable<OnItemClickEvent> movieSelectionStream() {
        return itemClicks.asObservable();
    }

}
