package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.core.PerActivity;
import com.dsvoronin.udacitymovies.data.entities.Movie;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module
public class GridModule {
    private final GridActivity activity;

    public GridModule(GridActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    GridActivity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    GridModelFragment provideGridModelFragment() {
        return new GridModelFragment();
    }
}
