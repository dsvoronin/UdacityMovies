package com.dsvoronin.udacitymovies;

import android.app.Application;
import android.content.Context;

import com.dsvoronin.udacitymovies.data.DataModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class MoviesApp extends Application {

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MoviesApp application = (MoviesApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        Timber.plant(BuildConfig.DEBUG ? new Timber.DebugTree() : new CrashReportingtree());
    }

    public static MoviesApp get(Context context) {
        return (MoviesApp) context.getApplicationContext();
    }

    public AppComponent component() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .uIModule(new UIModule())
                .dataModule(new DataModule())
                .build();
    }
}
