package com.dsvoronin.udacitymovies.data;

public class OnGridClickEvent<T> {
    public final int position;
    public final T data;

    public OnGridClickEvent(int position, T data) {
        this.position = position;
        this.data = data;
    }
}
