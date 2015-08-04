package com.dsvoronin.udacitymovies;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.dsvoronin.udacitymovies.core.DeviceClass;

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
    Configuration provideConfiguration(Application application) {
        return application.getResources().getConfiguration();
    }

    @Provides
    @Singleton
    DeviceClass provideIsTablet(Configuration configuration) {
        int smallestWidth = configuration.smallestScreenWidthDp;
        if (smallestWidth > 720) return DeviceClass.TABLET_10;
        if (smallestWidth > 600) return DeviceClass.TABLET_7;
        return DeviceClass.PHONE;
    }

//    @Provides
//    @Singleton
//    DeviceClass provideIsTablet(DisplayMetrics metrics) {
//        float scaleFactor = metrics.density;
//        float widthDp = metrics.widthPixels / scaleFactor;
//        float heightDp = metrics.heightPixels / scaleFactor;
//
//        float smallestWidth = Math.min(widthDp, heightDp);
//
//        if (smallestWidth > 720) return DeviceClass.TABLET_10;
//        if (smallestWidth > 600) return DeviceClass.TABLET_7;
//        return DeviceClass.PHONE;
//    }
}
