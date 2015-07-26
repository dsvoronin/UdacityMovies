package com.dsvoronin.udacitymovies;

import android.util.DisplayMetrics;

import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.dsvoronin.udacitymovies.data.DataModule;
import com.dsvoronin.udacitymovies.data.MovieDBService;
import com.dsvoronin.udacitymovies.detail.MovieDetailFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, UIModule.class, DataModule.class})
public interface AppComponent {

    void inject(MovieDetailFragment fragment);

    DisplayMetrics metrics();

    Boolean isTablet();

    MovieDBService service();

    @ImageQualifier
    String imageQualifier();

    @ImageEndpoint
    String imageEndpoint();

}
