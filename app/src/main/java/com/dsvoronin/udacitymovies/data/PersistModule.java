package com.dsvoronin.udacitymovies.data;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.entities.MovieStorIOSQLiteDeleteResolver;
import com.dsvoronin.udacitymovies.data.entities.MovieStorIOSQLiteGetResolver;
import com.dsvoronin.udacitymovies.data.entities.MovieStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PersistModule {

    @Provides
    @Singleton
    SQLiteOpenHelper provideSqLiteOpenHelper(Application context) {
        return new MoviesSQLiteOpenHelper(context);
    }

    @Provides
    @Singleton
    StorIOSQLite provideStorIOSQLite(SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Movie.class,
                        SQLiteTypeMapping.<Movie>builder()
                                .putResolver(new MovieStorIOSQLitePutResolver())
                                .getResolver(new MovieStorIOSQLiteGetResolver())
                                .deleteResolver(new MovieStorIOSQLiteDeleteResolver())
                                .build())
                .build();
    }
}
