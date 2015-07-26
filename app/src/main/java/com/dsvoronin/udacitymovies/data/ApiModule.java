package com.dsvoronin.udacitymovies.data;

import com.dsvoronin.udacitymovies.BuildConfig;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class ApiModule {

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
