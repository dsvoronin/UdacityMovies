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
                .table(MoviesTable.TABLE_NAME)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected UpdateQuery mapToUpdateQuery(@NonNull Movie object) {
        return UpdateQuery.builder()
                .table(MoviesTable.TABLE_NAME)
                .where(MoviesTable.COL_ID + " = ?")
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

        contentValues.put(MoviesTable.COL_ID, object.id);
        contentValues.put(MoviesTable.COL_TITLE, object.title);
        contentValues.put(MoviesTable.COL_OVERVIEW, object.overview);
        contentValues.put(MoviesTable.COL_POSTER_PATH, object.posterPath.toString());
        contentValues.put(MoviesTable.COL_RELEASE_DATE, object.releaseDate);
        contentValues.put(MoviesTable.COL_VOTE_AVERAGE, object.voteAverage);

        return contentValues;
    }
}
