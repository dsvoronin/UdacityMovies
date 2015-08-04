package com.dsvoronin.udacitymovies.data.persist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;
import com.squareup.okhttp.HttpUrl;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class MoviesContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.dsvoronin.udacitymovies.movies_provider";

    private static final String PATH_MOVIES = "movies";
    private static final int URI_MATCHER_CODE_MOVIES = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(1);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_MOVIES, URI_MATCHER_CODE_MOVIES);
    }

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/movies");

    @NonNull
    public static final PutResolver<Movie> PUT_RESOLVER = new DefaultPutResolver<Movie>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull Movie object) {
            return InsertQuery.builder()
                    .uri(CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull Movie movie) {
            return UpdateQuery.builder()
                    .uri(CONTENT_URI)
                    .where(MoviesTable.COL_ID + " = ?")
                    .whereArgs(movie.id)
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull Movie object) {
            ContentValues contentValues = new ContentValues(6);

            contentValues.put(MoviesTable.COL_ID, object.id);
            contentValues.put(MoviesTable.COL_TITLE, object.title);
            contentValues.put(MoviesTable.COL_OVERVIEW, object.overview);
            contentValues.put(MoviesTable.COL_POSTER_PATH, object.posterPath.toString());
            contentValues.put(MoviesTable.COL_RELEASE_DATE, object.releaseDate);
            contentValues.put(MoviesTable.COL_VOTE_AVERAGE, object.voteAverage);

            return contentValues;
        }
    };

    @NonNull
    public static final GetResolver<Movie> GET_RESOLVER = new DefaultGetResolver<Movie>() {
        @NonNull
        @Override
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
    };

    @NonNull
    public static final DeleteResolver<Movie> DELETE_RESOLVER = new DefaultDeleteResolver<Movie>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull Movie tweet) {
            return DeleteQuery.builder()
                    .uri(CONTENT_URI)
                    .where(MoviesTable.COL_ID + " = ?")
                    .whereArgs(tweet.id)
                    .build();
        }
    };

    @Inject
    SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    public boolean onCreate() {
        MoviesApp.get(getContext()).component().inject(this);
        return true;
    }

    @NonNull
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_MOVIES:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                MoviesTable.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );

            default:
                return null;
        }
    }

    @NotNull
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final long insertedId;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_MOVIES:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                MoviesTable.TABLE_NAME,
                                null,
                                values
                        );
                break;

            default:
                return null;
        }

        if (insertedId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, insertedId);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int numberOfRowsAffected;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_MOVIES:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                MoviesTable.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs
                        );
                break;

            default:
                return 0;
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsAffected;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int numberOfRowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_MOVIES:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                MoviesTable.TABLE_NAME,
                                selection,
                                selectionArgs
                        );
                break;

            default:
                return 0;
        }

        if (numberOfRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}
