package com.dsvoronin.udacitymovies;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.DisplayMetrics;

import com.dsvoronin.udacitymovies.core.DeviceClass;
import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.dsvoronin.udacitymovies.data.DataModule;
import com.dsvoronin.udacitymovies.data.api.MovieDBService;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.persist.MoviesContentProvider;
import com.dsvoronin.udacitymovies.rx.RxFragment;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.squareup.leakcanary.RefWatcher;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.Observer;

@Singleton
@Component(modules = {AppModule.class, UIModule.class, DataModule.class})
public interface AppComponent {

    DisplayMetrics metrics();

    DeviceClass deviceClass();

    MovieDBService service();

    RefWatcher refWatcher();

    Observable<Movie> movieSelection();

    Observer<Movie> movieSelector();

    StorIOContentResolver storIOContentResolver();

    SQLiteOpenHelper sqliteOpenHelper();

    @ImageQualifier
    String imageQualifier();

    @ImageEndpoint
    String imageEndpoint();

    Locale locale();

    void inject(RxFragment fragment);

    void inject(MoviesContentProvider fragment);

}
