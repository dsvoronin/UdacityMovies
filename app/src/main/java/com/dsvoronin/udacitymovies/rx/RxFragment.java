package com.dsvoronin.udacitymovies.rx;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.dsvoronin.udacitymovies.MoviesApp;
import com.squareup.leakcanary.RefWatcher;

import rx.subjects.PublishSubject;

public abstract class RxFragment extends Fragment {

    private PublishSubject<RxActivity> attachStream = PublishSubject.create();

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
        RefWatcher refWatcher = MoviesApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    public PublishSubject<RxActivity> getAttachStream() {
        return attachStream;
    }

}
