package com.dsvoronin.udacitymovies.data.persist;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.squareup.okhttp.HttpUrl;

public class MovieStorIOSQLiteGetResolver extends DefaultGetResolver<Movie> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Movie mapFromCursor(@NonNull Cursor cursor) {
        Movie object = new Movie();

        object.id = cursor.getLong(cursor.getColumnIndex(MoviesTable.COL_ID));
        object.title = cursor.getString(cursor.getColumnIndex(MoviesTable.COL_TITLE));
        object.overview = cursor.getString(cursor.getColumnIndex(MoviesTable.COL_OVERVIEW));
        object.posterPath = HttpUrl.parse(cursor.getString(cursor.getColumnIndex(MoviesTable.COL_POSTER_PATH)));
        object.releaseDate = cursor.getString(cursor.getColumnIndex(MoviesTable.COL_RELEASE_DATE));
        object.voteAverage = cursor.getString(cursor.getColumnIndex(MoviesTable.COL_VOTE_AVERAGE));

        return object;
    }
}
