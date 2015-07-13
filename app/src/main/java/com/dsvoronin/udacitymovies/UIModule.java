package com.dsvoronin.udacitymovies;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UIModule {

    @Provides
    @Singleton
    DisplayMetrics provideDisplayMetrics(Application application) {
        DisplayMetrics metrics = new DisplayMetrics();

        ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(metrics);

        return metrics;
    }

    @Provides
    @Singleton
    Boolean provideIsTablet(DisplayMetrics metrics) {
        return metrics.widthPixels > metrics.heightPixels;
    }

}
