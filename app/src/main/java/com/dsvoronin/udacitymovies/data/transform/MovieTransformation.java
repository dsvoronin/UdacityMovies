package com.dsvoronin.udacitymovies.data.transform;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dsvoronin.udacitymovies.data.dto.TMDBMovie;
import com.dsvoronin.udacitymovies.data.entities.Movie;
import com.squareup.okhttp.HttpUrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import rx.functions.Func1;
import timber.log.Timber;

/**
 * Transforms transfer object to inner app's one
 */
public class MovieTransformation implements Func1<TMDBMovie, Movie> {

    /**
     * movie database returns this format to us as relase date
     */
    private static final String TMDB_RELEASE_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * we want to show year as it is, like "2015"
     */
    private static final String APP_RELEASE_DATE_FORMAT = "yyyy";

    private final SimpleDateFormat yearFormat;
    private final SimpleDateFormat tmdbFormat;

    private final String imageEndpoint;
    private final String imageQualifier;

    public MovieTransformation(Locale locale, String imageEndpoint, String imageQualifier) {
        this.imageEndpoint = imageEndpoint;
        this.imageQualifier = imageQualifier;
        yearFormat = new SimpleDateFormat(APP_RELEASE_DATE_FORMAT, locale);
        tmdbFormat = new SimpleDateFormat(TMDB_RELEASE_DATE_FORMAT, locale);
    }

    @Override
    public Movie call(TMDBMovie movie) {
        return new Movie(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                injectImagePath(movie.getPosterPath()),
                formatReleaseDate(movie.getReleaseDate()),
                movie.getVoteAverage());
    }

    /**
     * injects full path to image with qualifier as described in implementation details
     * https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true#h.gubw0i1jurv0
     *
     * @param posterPath posterPath like: “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
     * @return valid HttpUrl
     */
    private HttpUrl injectImagePath(String posterPath) {
        return HttpUrl.parse(imageEndpoint + imageQualifier + posterPath);
    }

    /**
     * tmdb gives us release date in yyyy-MM-dd format, we need only year here
     */
    @Nullable
    private String formatReleaseDate(@Nullable String source) {
        try {
            return TextUtils.isEmpty(source) ? null : yearFormat.format(tmdbFormat.parse(source));
        } catch (ParseException e) {
            // if server returns date in wrong format, we ignore this date
            Timber.e(e, "Can't parse release date"); //but send it to crashlytics to investigate!
            return null;
        }
    }
}
