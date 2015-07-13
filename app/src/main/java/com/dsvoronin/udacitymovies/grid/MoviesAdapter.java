package com.dsvoronin.udacitymovies.grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class MoviesAdapter extends BindableAdapter<Movie> implements Action1<List<Movie>> {

    private List<Movie> data = new ArrayList<>();
    private final Picasso picasso;
    private static final float ASPECT = 1.5f;
    private final int imageWidth;

    public MoviesAdapter(Context context, Picasso picasso, int imageWidth) {
        super(context);
        this.picasso = picasso;
        this.imageWidth = imageWidth;
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
        ImageView imageView = new ImageView(getContext());
        AbsListView.LayoutParams params = new GridView.LayoutParams(imageWidth, (int) (imageWidth * ASPECT));
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    public void bindView(Movie item, int position, View view) {
        picasso.load(item.posterPath)
                .fit()
                .placeholder(R.drawable.noposter)
                .error(R.drawable.noposter)
                .into((ImageView) view);
    }

    @Override
    public void call(List<Movie> movies) {
        data.clear();
        data.addAll(movies);
        notifyDataSetChanged();
    }
}
