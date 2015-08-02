package com.dsvoronin.udacitymovies.data.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

@StorIOSQLiteType(table = "movies")
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(@NonNull Parcel in) {
            return new Movie(in);
        }

        @NonNull
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @StorIOSQLiteColumn(name = "_id", key = true)
    public long id;

    @StorIOSQLiteColumn(name = "title")
    public String title;

    @StorIOSQLiteColumn(name = "overview")
    public String overview;

    @StorIOSQLiteColumn(name = "poster_path")
    @SerializedName("poster_path")
    public String posterPath;

    @StorIOSQLiteColumn(name = "release_date")
    @SerializedName("release_date")
    public String releaseDate;

    @StorIOSQLiteColumn(name = "vote_average")
    @SerializedName("vote_average")
    public String voteAverage;

    public Movie() {
    }

    public Movie(long id, String title, String overview, String posterPath, String releaseDate, String voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    protected Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return id == movie.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     * another way to get full image path.
     * not so good as Picasso injection.
     * this pojo don't want to know about such details
     */
    @Deprecated
    public String getPosterFullUrl(String endpoint, String qualifier) {
        return endpoint + qualifier + posterPath;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
