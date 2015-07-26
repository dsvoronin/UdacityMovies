package com.dsvoronin.udacitymovies.data;

import android.app.Application;

import com.dsvoronin.udacitymovies.BuildConfig;
import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.jakewharton.byteunits.DecimalByteUnit;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import static java.util.concurrent.TimeUnit.SECONDS;

@Module
public class DataModule {
    static final int DISK_CACHE_SIZE = (int) DecimalByteUnit.MEGABYTES.toBytes(50);

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }

    @Provides
    @Singleton
    @ImageEndpoint
    String provideImageEndpoint() {
        return "http://image.tmdb.org/t/p/";
    }

    @Provides
    @Singleton
    @ImageQualifier
    String provideImageQualifier() {
        return "w185";
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides
    @Singleton
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint("http://api.themoviedb.org/3/");
    }

    @Provides
    @Singleton
    RequestInterceptor provideApiKeyInjector() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("api_key", BuildConfig.API_KEY);
            }
        };
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint, OkHttpClient okHttpClient, RequestInterceptor interceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(interceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    @Provides
    @Singleton
    MovieDBService provideService(RestAdapter restAdapter) {
        return restAdapter.create(MovieDBService.class);
    }

}
