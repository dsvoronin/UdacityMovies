package com.dsvoronin.udacitymovies;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dsvoronin.udacitymovies.data.Movie;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends BindableAdapter<Movie> {

    private List<Movie> data = new ArrayList<>();
    private Picasso picasso;
    private int imageSize;
    private final float ASPECT = 1.5f;
    private final boolean isTablet;

    public MoviesAdapter(Context context) {
        super(context);
        picasso = new Picasso.Builder(getContext())
                .downloader(new OkHttpDownloader(new OkHttpClient()))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("Picasso", "Can't load image: " + uri, exception);
                    }
                })
                .build();

        isTablet = isTablet(context);
        if (isTablet) {
            imageSize = getMetrics(context).widthPixels / 4;
        } else {
            imageSize = getMetrics(context).widthPixels / 2;
        }
    }

    public void replace(List<Movie> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
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
        return getItem(position).id.hashCode();
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        ImageView view = new ImageView(getContext());
//        GridView.LayoutParams params = new AbsListView.LayoutParams(imageSize, imageSize);
//        view.setLayoutParams(params);
//        view.setAdjustViewBounds(true);
        return view;
    }

    @Override
    public void bindView(Movie item, int position, View view) {
        picasso.load(item.url)
                .resize(imageSize, (int) (imageSize * ASPECT))
                .into((ImageView) view);
    }

    private DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private boolean isTablet(Context context) {
        DisplayMetrics metrics = getMetrics(context);
        return metrics.widthPixels > metrics.heightPixels;
    }
}
