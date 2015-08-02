package com.dsvoronin.udacitymovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class MoviesSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "udacity_movies";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_DB = "create table movies(" +
            "_id integer primary key, " +
            "title text, " +
            "overview text, " +
            "poster_path text, " +
            "release_date text, " +
            "vote_average text);";

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
