package com.dsvoronin.udacitymovies.rx;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class FlatIterable<T> implements Func1<List<T>, Observable<T>> {
    @Override
    public Observable<T> call(List<T> ts) {
        return Observable.from(ts);
    }
}
