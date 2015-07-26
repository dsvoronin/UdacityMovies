package com.dsvoronin.udacitymovies.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import javax.inject.Provider;

public abstract class ModelRetainedContainer<P extends Presenter, T extends Model<P>> extends Fragment {

    private T model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public static <
            C extends ModelRetainedContainer<P, M>,
            P extends Presenter,
            M extends Model<P>>
    M getOrCreateModel(
            FragmentManager fragmentManager,
            String tag,
            Provider<C> containerProvider,
            Provider<M> modelProvider) {

        C modelFragment;
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            modelFragment = containerProvider.get();
            modelFragment.setModel(modelProvider.get());
            fragmentManager.beginTransaction()
                    .add(modelFragment, tag)
                    .commit();
        } else {
            modelFragment = (C) fragment;
        }
        return modelFragment.getModel();
    }

}
