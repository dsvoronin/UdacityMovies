package com.dsvoronin.udacitymovies.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class ModelRetainedContainer<T extends Model> extends Fragment {

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

}
