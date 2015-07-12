package com.dsvoronin.udacitymovies;

import android.app.Application;

import timber.log.Timber;

public class MoviesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
