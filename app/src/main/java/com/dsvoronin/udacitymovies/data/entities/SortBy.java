package com.dsvoronin.udacitymovies.data.entities;

public enum SortBy {
    POPULARITY_DESC("popularity.desc"), VOTE_AVERAGE_DESC("vote_average.desc");

    private final String serializeName;

    SortBy(String serializeName) {
        this.serializeName = serializeName;
    }

    @Override
    public String toString() {
        return serializeName;
    }
}
