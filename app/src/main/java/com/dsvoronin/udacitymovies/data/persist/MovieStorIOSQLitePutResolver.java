package com.dsvoronin.udacitymovies.data.persist;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

public class MovieStorIOSQLitePutResolver extends DefaultPutResolver<Movie> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected InsertQuery mapToInsertQuery(@NonNull Movie object) {
        return InsertQuery.builder()
                .table("movies")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected UpdateQuery mapToUpdateQuery(@NonNull Movie object) {
        return UpdateQuery.builder()
                .table("movies")
                .where("_id = ?")
                .whereArgs(object.id)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull Movie object) {
        ContentValues contentValues = new ContentValues(6);

        contentValues.put("title", object.title);
        contentValues.put("vote_average", object.voteAverage);
        contentValues.put("overview", object.overview);
        contentValues.put("_id", object.id);
        contentValues.put("release_date", object.releaseDate);
        contentValues.put("poster_path", object.posterPath.toString());

        return contentValues;
    }
}
