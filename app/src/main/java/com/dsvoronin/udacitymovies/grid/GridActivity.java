package com.dsvoronin.udacitymovies.grid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.MasterCallbacks;
import com.dsvoronin.udacitymovies.core.RxActivity;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.detail.DetailsFragment;
import com.dsvoronin.udacitymovies.detail.DetailsActivity;


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
 * <p/>
 * This activity also implements the required
 * {@link MasterCallbacks} interface
 * to listen for item selections.
 */
public class GridActivity extends RxActivity implements MasterCallbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        setTitle(R.string.app_name);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link MasterCallbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsFragment.ARG_ITEM, movie);
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DetailsActivity.class);
            detailIntent.putExtra(DetailsFragment.ARG_ITEM, movie);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sorting, menu);
        return true;
    }
}