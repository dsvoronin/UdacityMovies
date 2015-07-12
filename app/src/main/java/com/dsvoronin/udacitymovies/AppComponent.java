package com.dsvoronin.udacitymovies;

import com.dsvoronin.udacitymovies.data.DataModule;
import com.dsvoronin.udacitymovies.grid.MoviesGridFragment;
import com.dsvoronin.udacitymovies.grid.MoviesModelFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, UIModule.class, DataModule.class})
public interface AppComponent {

    void inject(MoviesGridFragment fragment);

    void inject(MoviesModelFragment fragment);
}
