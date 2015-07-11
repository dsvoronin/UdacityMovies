package com.dsvoronin.udacitymovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class MovieHolder extends RecyclerView.ViewHolder {

        public MovieHolder(View itemView) {
            super(itemView);
        }
    }
}
