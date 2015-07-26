package com.dsvoronin.udacitymovies.detail;

import com.dsvoronin.udacitymovies.core.Presenter;

import rx.Observable;

public interface DetailsPresenter extends Presenter {

    /**
     * Movie selection Id's will come from this stream
     */
    Observable<Long> idStream();
}
