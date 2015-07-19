package com.dsvoronin.udacitymovies.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dsvoronin.udacitymovies.MoviesApp;
import com.squareup.leakcanary.RefWatcher;

import rx.subjects.PublishSubject;

public abstract class RxFragment<V extends View> extends Fragment {

    private PublishSubject<RxActivity> attachStream = PublishSubject.create();
    private PublishSubject<V> viewCreatedStream = PublishSubject.create();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachStream.onNext((RxActivity) activity);
    }

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        V view = createView(container);
        viewCreatedStream.onNext(view);
        return view.getView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreatedStream.onCompleted();
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

    public PublishSubject<V> getViewCreatedStream() {
        return viewCreatedStream;
    }

    protected abstract V createView(@Nullable ViewGroup container);
}
