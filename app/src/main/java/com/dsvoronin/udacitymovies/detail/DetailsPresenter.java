package com.dsvoronin.udacitymovies.detail;

import android.view.MenuItem;

import com.dsvoronin.udacitymovies.core.Presenter;

import rx.Observable;

public interface DetailsPresenter extends Presenter {

    Observable<MenuItem> favouritesClicks();
}
