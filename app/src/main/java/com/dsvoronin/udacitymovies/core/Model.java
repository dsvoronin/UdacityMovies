package com.dsvoronin.udacitymovies.core;

public interface Model<T extends Presenter> {

    void attachPresenter(T presenter);

    void detachPresenter();

}
