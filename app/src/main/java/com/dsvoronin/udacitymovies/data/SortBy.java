package com.dsvoronin.udacitymovies.data;

public enum SortBy {
    POPULARITY_DESC("popularity.desc");

    private final String serializeName;

    SortBy(String serializeName) {
        this.serializeName = serializeName;
    }

    @Override
    public String toString() {
        return serializeName;
    }
}
