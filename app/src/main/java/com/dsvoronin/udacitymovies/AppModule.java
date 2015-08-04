package com.dsvoronin.udacitymovies;

import android.app.Application;

import com.squareup.leakcanary.RefWatcher;

import java.util.Locale;

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

    /**
     * App don't know how to react on "on-the-fly" locale change yet, so getDefault() is ok
     * todo http://stackoverflow.com/questions/14389349/android-get-current-locale-not-default
     */
    @Provides
    @Singleton
    Locale provideLocale() {
        return Locale.getDefault();
    }
}
