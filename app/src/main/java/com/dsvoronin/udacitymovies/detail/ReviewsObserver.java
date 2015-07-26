package com.dsvoronin.udacitymovies.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dsvoronin.udacitymovies.R;
import com.dsvoronin.udacitymovies.data.entities.Review;
import com.dsvoronin.udacitymovies.databinding.ReviewRowBinding;

import java.util.List;

import rx.Observer;
import timber.log.Timber;

class ReviewsObserver implements Observer<List<Review>> {
    private final LayoutInflater inflater;
    private final ViewGroup parent;
    private final Context context;

    ReviewsObserver(LayoutInflater inflater, ViewGroup parent) {
        this.inflater = inflater;
        this.parent = parent;
        this.context = parent.getContext();
    }

    private void buildReviewRow(LayoutInflater inflater, ViewGroup parent, final Review review) {
        ReviewRowBinding rowBinding = ReviewRowBinding.inflate(inflater, parent, true);
        rowBinding.setReview(review);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Can't load trailers");
        Toast.makeText(context, R.string.reviews_load_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(List<Review> reviews) {
        parent.removeAllViews();
        for (Review review : reviews) {
            buildReviewRow(inflater, parent, review);
        }
    }
}
