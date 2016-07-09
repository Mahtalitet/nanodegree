package com.cyberurbi.udacity.udacityprojectoneandmore;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Marcin Gruszecki
 * for Project Two
 */
public class Movie {


    private String sOriginalTitle;
    private String sPosterPath;
    private String sReleaseDate;
    private String sRating;
    private String sOverview;
    private float  fPopularity;
    private String sId;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    private boolean isFavorite;

    public Movie(){

       super();
       setFavorite(false);

    }

    public String getsOriginalTitle() {
        return sOriginalTitle;
    }

    public void setsOriginalTitle(String sOriginalTitle) {
        this.sOriginalTitle = sOriginalTitle;
    }

    public String getsPosterPath() {
        return sPosterPath;
    }

    public void setsPosterPath(String sPosterPath) {
        this.sPosterPath = sPosterPath;
    }

    public String getsReleaseDate() {
        return sReleaseDate;
    }

    public void setsReleaseDate(String sReleaseDate) {
        this.sReleaseDate = sReleaseDate;
    }

    public String getsRating() {
        return sRating;
    }

    public void setsRating(String sRating) {
        this.sRating = sRating;
    }

    public float getPopularity() {
        return fPopularity;
    }

    public void setPopularity(float fPopularity) {
        this.fPopularity = fPopularity;
    }

    public String getsOverview() {
        return sOverview;
    }

    public void setsOverview(String sOverview) {
        this.sOverview = sOverview;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }


    // --- Movie Data Contract --->

    public static final String CONTENT_AUTHORITY = "com.cyberurbi.udacity.udacityprojectoneandmore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = MovieEntry.TABLE_NAME;
    public static final String PATH_TRAILER = TrailerEntry.TABLE_NAME;
    public static final String PATH_REVIEW = ReviewEntry.TABLE_NAME;

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + '/' + CONTENT_AUTHORITY + '/' + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + '/' + CONTENT_AUTHORITY + '/' + PATH_MOVIE;


        public static Uri buildMovieById(String sMovieId){
            return CONTENT_URI.buildUpon().appendPath(sMovieId).build();
        }

        public static Uri buildIsFavouriteMovieById(String sMovieId){

            Uri u = CONTENT_URI.buildUpon().appendPath(sMovieId).appendPath(MovieEntry.COLUMN_FAVORITE).build();
            Log.v("#1736",u.toString());
            return u;
        }

        public static Uri buildSetFavouriteMovieById(String sMovieId){
            return CONTENT_URI.buildUpon().appendPath(sMovieId).appendPath(GlobalData.cFavoriteYes).build();
        }



        public static final String TABLE_NAME = "Movies";
        public static final String COLUMN_ID = "sId";
        public static final String COLUMN_TITLE = "sOriginalTitle";
        public static final String COLUMN_POSTER_PATH = "sPosterPath";
        public static final String COLUMN_RELEASE_DATE = "sReleaseDate";
        public static final String COLUMN_RATING = "sRating";
        public static final String COLUMN_OVERVIEW = "sOverview";
        public static final String COLUMN_POPULARITY = "sPopularity";
        public static final String COLUMN_FAVORITE = "sFavorite";

    }

    public static final class TrailerEntry implements BaseColumns {

        public static Uri buildMovieTrailerById(String sMovieId){
            return CONTENT_URI.buildUpon().appendPath(sMovieId).build();
        }

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + '/' + CONTENT_AUTHORITY + '/' + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + '/' + CONTENT_AUTHORITY + '/' + PATH_TRAILER;

        public static final String TABLE_NAME = "Trailers";
        public static final String COLUMN_MOVIE_ID = "sMovieId";
        public static final String COLUMN_TRAILER_NAME = "sName";
        public static final String COLUMN_YOUTUBE_KEY = "sKey";

    }

    public static final class ReviewEntry implements BaseColumns {

        public static Uri buildMovieReviewById(String sMovieId){
            return CONTENT_URI.buildUpon().appendPath(sMovieId).build();
        }

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + '/' + CONTENT_AUTHORITY + '/' + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + '/' + CONTENT_AUTHORITY + '/' + PATH_REVIEW;

        public static final String TABLE_NAME = "Reviews";
        public static final String COLUMN_MOVIE_ID = "sMovieId";
        public static final String COLUMN_AUTHOR = "sAuthor";
        public static final String COLUMN_REVIEW = "sReview";

    }

    public ContentValues getMovieEntry(){

        ContentValues c = new ContentValues();

        c.put(MovieEntry.COLUMN_ID, getsId() );
        c.put(MovieEntry.COLUMN_TITLE, getsOriginalTitle());
        c.put(MovieEntry.COLUMN_POSTER_PATH, getsPosterPath());
        c.put(MovieEntry.COLUMN_OVERVIEW, getsOverview());
        c.put(MovieEntry.COLUMN_RELEASE_DATE, getsReleaseDate());
        c.put(MovieEntry.COLUMN_RATING, getsRating());
        if(isFavorite()) {
            c.put(MovieEntry.COLUMN_FAVORITE, GlobalData.cFavoriteYes);
        } else {
            c.put(MovieEntry.COLUMN_FAVORITE, GlobalData.cFavoriteNo);
        }
        c.put(MovieEntry.COLUMN_POPULARITY, getPopularity());

        return c;

    }


}
