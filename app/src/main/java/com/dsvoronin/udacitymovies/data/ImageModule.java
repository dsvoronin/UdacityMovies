package com.dsvoronin.udacitymovies.data;

import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ImageModule {

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
}
