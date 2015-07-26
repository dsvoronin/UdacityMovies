package com.dsvoronin.udacitymovies.rx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class RxActivity extends AppCompatActivity {

    private final PublishSubject<Bundle> onCreate = PublishSubject.create();
    private final PublishSubject<Menu> onCreateOptionsMenu = PublishSubject.create();
    private final PublishSubject<MenuItem> onOptionsItemSelected = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate.onNext(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        onCreateOptionsMenu.onNext(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onOptionsItemSelected.onNext(item);
        return true;
    }

    public Observable<Bundle> onCreateStream() {
        return onCreate.asObservable();
    }

    public Observable<Menu> onCreateOptionsMenuStream() {
        return onCreateOptionsMenu.asObservable();
    }

    public Observable<MenuItem> onOptionsItemSelectedStream() {
        return onOptionsItemSelected.asObservable();
    }
}
