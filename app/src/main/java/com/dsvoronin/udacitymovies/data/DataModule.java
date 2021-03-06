package com.dsvoronin.udacitymovies.data;

import com.dsvoronin.udacitymovies.data.api.ApiModule;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.data.persist.PersistModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Observer;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

@Module(includes = {ImageModule.class, NetworkModule.class, ApiModule.class, PersistModule.class})
public class DataModule {

    private final Subject<Movie, Movie> movieSelectionSubject;

    public DataModule() {
        this.movieSelectionSubject = BehaviorSubject.create();
    }

    @Provides
    @Singleton
    Observable<Movie> provideMovieSelection() {
        return movieSelectionSubject.asObservable();
    }

    @Provides
    @Singleton
    Observer<Movie> provideMovieSelector() {
        return movieSelectionSubject;
    }
}
