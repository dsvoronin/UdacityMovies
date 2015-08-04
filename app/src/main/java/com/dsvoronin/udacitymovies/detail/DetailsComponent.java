package com.dsvoronin.udacitymovies.detail;

import com.dsvoronin.udacitymovies.AppComponent;
import com.dsvoronin.udacitymovies.core.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {AppComponent.class}, modules = {DetailsModule.class})
public interface DetailsComponent extends AppComponent {

    void inject(DetailsFragment fragment);

}
