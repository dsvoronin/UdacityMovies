package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.data.SortBy;

import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.widget.OnItemClickEvent;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class MoviesGridPresenter {

    public MoviesGridPresenter() {
    }

    BehaviorSubject<SortBy> sortingSelection = BehaviorSubject.create();
    PublishSubject<OnClickEvent> reloads = PublishSubject.create();
    PublishSubject<OnItemClickEvent> itemClicks = PublishSubject.create();

    /**
     * Emits user selections of sorting options
     */
    public Observable<SortBy> sortingSelectionStream() {
        return sortingSelection.asObservable()
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
