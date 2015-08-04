package com.dsvoronin.udacitymovies.data.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class MoviesSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "udacity_movies";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_DB = "create table " + MoviesTable.TABLE_NAME + "(" +
            MoviesTable.COL_ID + " integer primary key, " +
            MoviesTable.COL_TITLE + " text, " +
            MoviesTable.COL_OVERVIEW + " text, " +
            MoviesTable.COL_POSTER_PATH + " text, " +
            MoviesTable.COL_RELEASE_DATE + " text, " +
            MoviesTable.COL_VOTE_AVERAGE + " text);";

    public MoviesSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists movies");
        onCreate(db);
    }
}
