package com.dsvoronin.udacitymovies;

import android.app.Application;
import android.content.Context;

import com.dsvoronin.udacitymovies.data.DataModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class MoviesApp extends Application {

    private RefWatcher refWatcher;
    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        Timber.plant(BuildConfig.DEBUG ? new Timber.DebugTree() : new CrashReportingtree());
        component = buildComponent();
    }

    public static MoviesApp get(Context context) {
        return (MoviesApp) context.getApplicationContext();
    }

    public AppComponent component() {
        if (component == null) {
            synchronized (MoviesApp.class) {
                if (component == null) {
                    component = buildComponent();
                }
            }
        }

        //noinspection ConstantConditions
        return component;
    }

    private AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this, refWatcher))
                .uIModule(new UIModule())
                .dataModule(new DataModule())
                .build();
    }
}
