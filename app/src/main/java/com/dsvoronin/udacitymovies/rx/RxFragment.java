package com.dsvoronin.udacitymovies.rx;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.dsvoronin.udacitymovies.AppComponent;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class RxFragment extends Fragment {

    private PublishSubject<RxActivity> attachStream = PublishSubject.create();
    private PublishSubject<MenuItem> optionsItemSelectedStream = PublishSubject.create();

    @Inject RefWatcher refWatcher;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachStream.onNext((RxActivity) activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attachStream.onCompleted();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getComponent().inject(this);
        refWatcher.watch(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        optionsItemSelectedStream.onNext(item);
        return true;
    }

    public Observable<RxActivity> getAttachStream() {
        return attachStream.asObservable();
    }

    public Observable<MenuItem> getOptionsItemSelectedStream() {
        return optionsItemSelectedStream.asObservable();
    }

    private AppComponent getComponent() {
        return ((MoviesApp) getActivity().getApplication()).component();
    }
}
