package com.dsvoronin.udacitymovies;

import android.app.Application;

import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final MoviesApp app;
    private final RefWatcher refWatcher;

    public AppModule(MoviesApp app, RefWatcher refWatcher) {
        this.app = app;
        this.refWatcher = refWatcher;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    RefWatcher provideRefWatcher() {
        return refWatcher;
    }

}
