package com.dsvoronin.udacitymovies.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyDataSource implements DataSource {

    public static Map<String, Movie> dataMap = new HashMap<>();

    public DummyDataSource() {
        dataMap.put("1", new Movie("1", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        dataMap.put("2", new Movie("2", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        dataMap.put("3", new Movie("3", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        dataMap.put("4", new Movie("4", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        dataMap.put("5", new Movie("5", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        dataMap.put("6", new Movie("6", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
    }

    @Override
    public List<Movie> getMovies() {
        return new ArrayList<>(dataMap.values());
    }
}
