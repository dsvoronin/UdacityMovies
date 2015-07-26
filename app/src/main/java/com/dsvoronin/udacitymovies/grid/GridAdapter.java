package com.dsvoronin.udacitymovies.grid;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.entities.Movie;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> implements Action1<List<Movie>> {

    private final RequestManager glide;
    private final List<Movie> movies = new ArrayList<>();
    private final PublishSubject<Movie> selectionStream = PublishSubject.create();

    public GridAdapter(RequestManager picasso) {
        this.glide = picasso;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false));
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).id;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        glide.load(movies.get(position).posterPath)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into((ImageView) holder.itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                selectionStream.onNext(getItem(position));
            }
        });
    }

    public Observable<Movie> getSelectionStream() {
        return selectionStream.asObservable();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void call(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
