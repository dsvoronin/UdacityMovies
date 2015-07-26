package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.core.ModelRetainedContainer;

import javax.inject.Provider;

public class GridModelFragment extends ModelRetainedContainer<GridPresenter, GridModel> {

    //todo move to dagger
    public static Provider<GridModelFragment> getProvider() {
        return new Provider<GridModelFragment>() {
            @Override
            public GridModelFragment get() {
                return new GridModelFragment();
            }
        };
    }
}
