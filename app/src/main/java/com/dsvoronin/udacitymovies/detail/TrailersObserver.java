package com.dsvoronin.udacitymovies.detail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.entities.Trailer;
import com.dsvoronin.udacitymovies.databinding.TrailerRowBinding;

import java.util.List;

import rx.Observer;
import timber.log.Timber;

class TrailersObserver implements Observer<List<Trailer>> {
    private final LayoutInflater inflater;
    private final ViewGroup parent;
    private final Context context;

    TrailersObserver(LayoutInflater inflater, ViewGroup parent) {
        this.inflater = inflater;
        this.parent = parent;
        this.context = parent.getContext();
    }

    private void buildTrailerRow(LayoutInflater inflater, ViewGroup parent, final Trailer trailer) {
        TrailerRowBinding binding = TrailerRowBinding.inflate(inflater, parent, true);
        binding.setTrailer(trailer);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                startYoutube(trailer);
            }
        });
    }

    private void startYoutube(Trailer trailer) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailer.key));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.key));
            context.startActivity(intent);
        }
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Can't load trailers");
        Toast.makeText(context, R.string.trailers_load_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(List<Trailer> trailers) {
        parent.removeAllViews();
        for (Trailer trailer : trailers) {
            buildTrailerRow(inflater, parent, trailer);
        }
    }
}
