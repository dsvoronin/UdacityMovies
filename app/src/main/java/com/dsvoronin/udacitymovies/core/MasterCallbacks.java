package com.dsvoronin.udacitymovies.core;

import com.dsvoronin.udacitymovies.data.Movie;

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections.
 */
public interface MasterCallbacks {
    /**
     * Callback for when an item has been selected.
     */
    void onItemSelected(Movie movie);

    /**
     * A dummy implementation of the {@link MasterCallbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    MasterCallbacks DUMMY_CALLBACKS = new MasterCallbacks() {
        @Override
        public void onItemSelected(Movie movie) {
        }
    };
}
