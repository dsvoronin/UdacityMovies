package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.core.ModelRetainedContainer;

import javax.inject.Provider;

public class MoviesModelFragment extends ModelRetainedContainer<MoviesGridModel> {

    //todo move to dagger
    public static Provider<MoviesModelFragment> getProvider() {
        return new Provider<MoviesModelFragment>() {
            @Override
            public MoviesModelFragment get() {
                return new MoviesModelFragment();
            }
        };
    }
}
