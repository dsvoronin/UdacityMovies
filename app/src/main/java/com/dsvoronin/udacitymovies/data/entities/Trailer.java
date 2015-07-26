package com.dsvoronin.udacitymovies.data.entities;

public class Trailer {

    private final String key;
    private final String site;

    public Trailer(String key, String site) {
        this.key = key;
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }
}
