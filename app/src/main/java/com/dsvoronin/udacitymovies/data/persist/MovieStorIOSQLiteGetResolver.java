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

        object.title = cursor.getString(cursor.getColumnIndex("title"));
        object.voteAverage = cursor.getString(cursor.getColumnIndex("vote_average"));
        object.overview = cursor.getString(cursor.getColumnIndex("overview"));
        object.id = cursor.getLong(cursor.getColumnIndex("_id"));
        object.releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
        object.posterPath = HttpUrl.parse(cursor.getString(cursor.getColumnIndex("poster_path")));

        return object;
    }
}
