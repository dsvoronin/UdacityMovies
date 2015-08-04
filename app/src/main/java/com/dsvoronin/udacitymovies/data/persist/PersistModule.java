package com.dsvoronin.udacitymovies.data.persist;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
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

    @Provides
    @Singleton
    StorIOContentResolver provideStorIOContentResolver(Application application) {
        return DefaultStorIOContentResolver.builder()
                .contentResolver(application.getContentResolver())
                .addTypeMapping(Movie.class,
                        ContentResolverTypeMapping.<Movie>builder()
                                .putResolver(MoviesContentProvider.PUT_RESOLVER)
                                .getResolver(MoviesContentProvider.GET_RESOLVER)
                                .deleteResolver(MoviesContentProvider.DELETE_RESOLVER)
                                .build())
                .build();
    }

}
