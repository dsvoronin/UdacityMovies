package com.dsvoronin.udacitymovies.detail;

import android.os.Bundle;
import android.view.MenuItem;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.grid.GridActivity;
import com.dsvoronin.udacitymovies.rx.RxActivity;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link GridActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link DetailsFragment}.
 */
public class DetailsActivity extends RxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
