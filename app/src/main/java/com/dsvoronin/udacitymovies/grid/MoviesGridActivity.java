package com.dsvoronin.udacitymovies.grid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.core.MasterCallbacks;
import com.dsvoronin.udacitymovies.data.Movie;
import com.dsvoronin.udacitymovies.data.SortBy;
import com.dsvoronin.udacitymovies.detail.MovieDetailActivity;
import com.dsvoronin.udacitymovies.detail.MovieDetailFragment;

import rx.Observable;
import rx.subjects.PublishSubject;


/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MoviesGridFragment} and the item details
 * (if present) is a {@link MovieDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link MasterCallbacks} interface
 * to listen for item selections.
 */
public class MoviesGridActivity extends AppCompatActivity implements MasterCallbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private PublishSubject<SortBy> sortBySubject = PublishSubject.create();

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
            arguments.putParcelable(MovieDetailFragment.ARG_ITEM, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailFragment.ARG_ITEM, movie);
            startActivity(detailIntent);
        }
    }

    public Observable<SortBy> getSortBySubject() {
        return sortBySubject.asObservable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sorting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                sortBySubject.onNext(SortBy.POPULARITY_DESC);
                return true;
            case R.id.sort_by_rating:
                sortBySubject.onNext(SortBy.VOTE_AVERAGE_DESC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
