package com.dsvoronin.udacitymovies.grid;

import android.os.Bundle;
import android.view.Menu;

import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.detail.DetailsActivity;
import com.dsvoronin.udacitymovies.detail.DetailsFragment;
import com.dsvoronin.udacitymovies.rx.RxActivity;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DetailsActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link GridFragment} and the item details
 * (if present) is a {@link DetailsFragment}.
 */
public class GridActivity extends RxActivity {

    private GridComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        component = buildComponent();
        component.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        setTitle(R.string.app_name);
    }

    public GridComponent component() {
        return component;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sorting, menu);
        return true;
    }

    private GridComponent buildComponent() {
        return DaggerGridComponent.builder()
                .appComponent(MoviesApp.get(this).component())
                .gridModule(new GridModule(this))
                .build();
    }
}
