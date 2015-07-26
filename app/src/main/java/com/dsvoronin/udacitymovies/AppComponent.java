package com.dsvoronin.udacitymovies;

import android.util.DisplayMetrics;

import com.dsvoronin.udacitymovies.core.DeviceClass;
import com.dsvoronin.udacitymovies.core.ImageEndpoint;
import com.dsvoronin.udacitymovies.core.ImageQualifier;
import com.dsvoronin.udacitymovies.data.DataModule;
import com.dsvoronin.udacitymovies.data.MovieDBService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, UIModule.class, DataModule.class})
public interface AppComponent {

    DisplayMetrics metrics();

    DeviceClass deviceClass();

    MovieDBService service();

    @ImageQualifier
    String imageQualifier();

    @ImageEndpoint
    String imageEndpoint();

}
