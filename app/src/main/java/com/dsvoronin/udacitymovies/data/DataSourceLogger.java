package com.dsvoronin.udacitymovies.data;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

/**
 * Simple logging to let us know what each source is returning
 */
public class DataSourceLogger<T> implements Observable.Transformer<List<T>, List<T>> {
    private final DataSource source;

    public DataSourceLogger(DataSource source) {
        this.source = source;
    }

    @Override
    public Observable<List<T>> call(Observable<List<T>> data) {
        if (data == null) {
            Timber.d(source + " does not have any data.");
        } else {
            Timber.d(source + " has the data you are looking for!");
        }
        return data;
    }
}
