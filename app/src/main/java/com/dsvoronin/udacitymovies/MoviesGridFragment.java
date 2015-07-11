package com.dsvoronin.udacitymovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MoviesGridFragment extends Fragment {

    private MoviesAdapter adapter = new MoviesAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        RecyclerView view = new RecyclerView(context);
        view.setLayoutManager(new GridLayoutManager(context, 2));
        view.setAdapter(adapter);
        return view;
    }
}
