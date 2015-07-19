package com.dsvoronin.udacitymovies.grid;

import android.view.MenuItem;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.RxActivity;
import com.dsvoronin.udacitymovies.core.RxFragment;
import com.dsvoronin.udacitymovies.data.SortBy;

import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.widget.OnItemClickEvent;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class MoviesGridPresenter {

    private PublishSubject<MenuItem> optionsItemSelected = PublishSubject.create();
    private PublishSubject<OnClickEvent> reloads = PublishSubject.create();
    private PublishSubject<OnItemClickEvent> itemClicks = PublishSubject.create();

    private CompositeSubscription subscription = new CompositeSubscription();

    public void attach(RxFragment<MoviesGridView> fragment) {
        subscription.add(fragment.getAttachStream().flatMap(new Func1<RxActivity, Observable<MenuItem>>() {
            @Override
            public Observable<MenuItem> call(RxActivity rxActivity) {
                return rxActivity.onOptionsItemSelectedStream();
            }
        }).subscribe(optionsItemSelected));

        subscription.add(fragment.getViewCreatedStream().flatMap(new Func1<MoviesGridView, Observable<OnItemClickEvent>>() {
            @Override
            public Observable<OnItemClickEvent> call(MoviesGridView moviesGridView) {
                return moviesGridView.itemClicksStream();
            }
        }).subscribe(itemClicks));
    }

    public void detach() {
        subscription.clear();
    }

    /**
     * Emits user selections of sorting options
     */
    public Observable<SortBy> sortingSelectionStream() {
        Observable<SortBy> popularitySteam = optionsItemSelected.filter(new Func1<MenuItem, Boolean>() {
            @Override
            public Boolean call(MenuItem menuItem) {
                return menuItem.getItemId() == R.id.sort_by_popularity;
            }
        }).map(new Func1<MenuItem, SortBy>() {
            @Override
            public SortBy call(MenuItem menuItem) {
                return SortBy.POPULARITY_DESC;
            }
        });
        Observable<SortBy> ratingStream = optionsItemSelected.filter(new Func1<MenuItem, Boolean>() {
            @Override
            public Boolean call(MenuItem menuItem) {
                return menuItem.getItemId() == R.id.sort_by_rating;
            }
        }).map(new Func1<MenuItem, SortBy>() {
            @Override
            public SortBy call(MenuItem menuItem) {
                return SortBy.VOTE_AVERAGE_DESC;
            }
        });

        return Observable.merge(popularitySteam, ratingStream)
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
