package com.dsvoronin.udacitymovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    public final long id;
    public final String title;
    public final String overview;
    @SerializedName("poster_path")
    public final String posterPath;
    @SerializedName("release_date")
    public final String releaseDate;
    @SerializedName("vote_average")
    public final String voteAverage;

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
    public void writeToParcel(Parcel dest, int flags) {
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
                ", posterPath='" + posterPath + '\'' +
                '}';
    }
}
