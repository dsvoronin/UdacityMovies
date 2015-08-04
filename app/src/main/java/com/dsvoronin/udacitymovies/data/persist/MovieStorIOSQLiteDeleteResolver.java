package com.dsvoronin.udacitymovies.data.persist;

import android.support.annotation.NonNull;

import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

public class MovieStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<Movie> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected DeleteQuery mapToDeleteQuery(@NonNull Movie object) {
        return DeleteQuery.builder()
                .table("movies")
                .where("_id = ?")
                .whereArgs(object.id)
                .build();
    }
}
