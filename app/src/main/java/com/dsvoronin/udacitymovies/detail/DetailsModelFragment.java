package com.dsvoronin.udacitymovies.detail;

import com.dsvoronin.udacitymovies.core.ModelRetainedContainer;

import javax.inject.Provider;

public class DetailsModelFragment extends ModelRetainedContainer<DetailsPresenter, DetailsModel> {

    public static Provider<DetailsModelFragment> getProvider() {
        return new Provider<DetailsModelFragment>() {
            @Override
            public DetailsModelFragment get() {
                return new DetailsModelFragment();
            }
        };
    }
}
