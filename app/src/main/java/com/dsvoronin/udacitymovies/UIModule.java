package com.dsvoronin.udacitymovies;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UIModule {

    @Provides
    @Singleton
    Picasso providePicasso(Application application, OkHttpClient okHttpClient) {
        return new Picasso.Builder(application)
                .requestTransformer(new Picasso.RequestTransformer() {
                    @Override
                    public Request transformRequest(Request request) {
                        return request.buildUpon()
                                .setUri(Uri.parse("http://image.tmdb.org/t/p/w185/" + request.uri.getPath()))
                                .build();
                    }
                })
                .downloader(new OkHttpDownloader(okHttpClient))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("Picasso", "Can't load image: " + uri, exception);
                    }
                })
                .build();
    }

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
    Boolean provideIsTable(DisplayMetrics metrics) {
        return metrics.widthPixels > metrics.heightPixels;
    }

}
