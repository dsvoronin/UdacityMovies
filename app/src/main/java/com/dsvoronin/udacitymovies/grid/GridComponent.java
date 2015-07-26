package com.dsvoronin.udacitymovies.grid;

import com.dsvoronin.udacitymovies.AppComponent;
import com.dsvoronin.udacitymovies.core.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {AppComponent.class})
public interface GridComponent extends AppComponent {

    void inject(GridFragment fragment);

}
