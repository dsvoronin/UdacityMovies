package com.dsvoronin.udacitymovies.grid;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dsvoronin.udacitymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class MoviesAdapter extends BindableAdapter<Movie> implements Action1<List<Movie>> {

    private List<Movie> data = new ArrayList<>();
    private final Picasso picasso;
    private int imageSize;
    private static final float ASPECT = 1.5f;

    public MoviesAdapter(Context context, Picasso picasso, DisplayMetrics metrics, Boolean isTablet) {
        super(context);
        this.picasso = picasso;

        if (isTablet) {
            imageSize = metrics.widthPixels / 4;
        } else {
            imageSize = metrics.widthPixels / 2;
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Movie getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return new ImageView(getContext());
    }

    @Override
    public void bindView(Movie item, int position, View view) {
        picasso.load(item.posterPath)
                .resize(imageSize, (int) (imageSize * ASPECT))
                .into((ImageView) view);
    }

    @Override
    public void call(List<Movie> movies) {
        data.clear();
        data.addAll(movies);
        notifyDataSetChanged();
    }
}
