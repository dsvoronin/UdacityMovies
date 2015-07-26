package com.dsvoronin.udacitymovies.detail;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dsvoronin.udacitymovies.AppModule;
import com.dsvoronin.udacitymovies.DaggerAppComponent;
import com.dsvoronin.udacitymovies.MoviesApp;
import com.dsvoronin.udacitymovies.UIModule;
import com.dsvoronin.udacitymovies.data.DataModule;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.dsvoronin.udacitymovies.databinding.FragmentMovieDetailBinding;
import com.dsvoronin.udacitymovies.grid.MoviesGridActivity;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MoviesGridActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "item";

    /**
     * The dummy content this fragment is presenting.
     */
    private Movie movie;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        DaggerAppComponent.builder()
                .appModule(new AppModule((MoviesApp) getActivity().getApplication()))
                .uIModule(new UIModule())
                .dataModule(new DataModule())
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            movie = getArguments().getParcelable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMovieDetailBinding binding = FragmentMovieDetailBinding.inflate(inflater);

        if (movie != null) {
            binding.setMovie(movie);
            Glide.with(this).load(movie.posterPath)
                    .into(binding.detailsPoster);
        }

        return binding.getRoot();
    }
}
