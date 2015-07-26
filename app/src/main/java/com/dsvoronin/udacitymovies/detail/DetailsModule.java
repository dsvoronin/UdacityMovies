package com.dsvoronin.udacitymovies.detail;

import com.dsvoronin.udacitymovies.core.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsModule {

    @Provides
    @PerActivity
    DetailsModelFragment provideModelFragment() {
        return new DetailsModelFragment();
    }
}
