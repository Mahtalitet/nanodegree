package com.cyberurbi.udacity.udacityprojectoneandmore;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


import java.util.List;

/**
 * Created by MG on 2015-08-01.
 */


public class MovieDataProvider extends ContentProvider {

    MoviesDbHelper dbHelper;
    UriMatcher uriMatcher;


    static final int MOVIE = 1;
    static final int MOVIE_ID = 2;
    static final int MOVIE_ID_FAVORITE = 3;
    static final int MOVIE_ID_TRAILERS = 4;
    static final int MOVIE_ID_REVIEWS = 5;
    static final int SET_MOVIE_ID_FAVORITE = 6;


    static UriMatcher buildUriMatcher() {

        UriMatcher theUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);



        theUriMatcher.addURI(Movie.CONTENT_AUTHORITY, Movie.PATH_MOVIE, MOVIE);
        theUriMatcher.addURI(Movie.CONTENT_AUTHORITY, Movie.PATH_MOVIE + "/#", MOVIE_ID);
        theUriMatcher.addURI(Movie.CONTENT_AUTHORITY, Movie.PATH_MOVIE + "/#/" + Movie.MovieEntry.COLUMN_FAVORITE, MOVIE_ID_FAVORITE);
        theUriMatcher.addURI(Movie.CONTENT_AUTHORITY, Movie.PATH_MOVIE + "/#/" + GlobalData.cFavoriteYes , SET_MOVIE_ID_FAVORITE);
        theUriMatcher.addURI(Movie.CONTENT_AUTHORITY, Movie.PATH_TRAILER + "/#", MOVIE_ID_TRAILERS);
        theUriMatcher.addURI(Movie.CONTENT_AUTHORITY, Movie.PATH_REVIEW + "/#", MOVIE_ID_REVIEWS);

        return theUriMatcher;

    }

    @Override
    public boolean onCreate() {

        dbHelper = new MoviesDbHelper(getContext());
        uriMatcher = buildUriMatcher();
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        final int i = uriMatcher.match(uri);
        Cursor theCursor;

        // -- database mode -->

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sMovieId;

        // <-- database mode



        switch (i){
            case MOVIE:
                theCursor =  db.query(Movie.MovieEntry.TABLE_NAME,null,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_ID:
                sMovieId = uri.getLastPathSegment();
                theCursor = db.query(Movie.MovieEntry.TABLE_NAME,null, Movie.MovieEntry.COLUMN_ID + " = ?",new String[]{sMovieId},null,null,sortOrder);
                break;
            case MOVIE_ID_FAVORITE:
                List<String> l = uri.getPathSegments();
                sMovieId = l.get(l.size()-2);
                theCursor = db.query(Movie.MovieEntry.TABLE_NAME, new String[]{Movie.MovieEntry.COLUMN_FAVORITE},Movie.MovieEntry.COLUMN_ID + " = ?",new String[]{sMovieId},null,null,sortOrder);
                break;
            case MOVIE_ID_TRAILERS:
                theCursor =  db.query(Movie.TrailerEntry.TABLE_NAME,null,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_ID_REVIEWS:
                theCursor = db.query(Movie.ReviewEntry.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                theCursor = null;

        }

        return theCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int i = uriMatcher.match(uri);

        switch (i){
            case MOVIE:
                return Movie.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return Movie.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_ID_TRAILERS:
                return Movie.TrailerEntry.CONTENT_TYPE;
            case MOVIE_ID_REVIEWS:
                return Movie.ReviewEntry.CONTENT_TYPE;
            case MOVIE_ID_FAVORITE:
                return Movie.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int i = uriMatcher.match(uri);
        Cursor theCursor;

        // -- database mode -->

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sMovieId;



        // <-- database mode

        switch (i){
            case MOVIE:
                db.insert(Movie.MovieEntry.TABLE_NAME,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Movie.MovieEntry.buildMovieById(values.getAsString(Movie.MovieEntry.COLUMN_ID));
            case MOVIE_ID_TRAILERS:
                db.insert(Movie.TrailerEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Movie.TrailerEntry.buildMovieTrailerById(values.getAsString(Movie.TrailerEntry.COLUMN_MOVIE_ID));
            case MOVIE_ID_REVIEWS:
                db.insert(Movie.ReviewEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Movie.ReviewEntry.buildMovieReviewById(values.getAsString(Movie.ReviewEntry.COLUMN_MOVIE_ID));
             default:

                 return null;
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // -- Not used in the project (M.G.) ---

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int i = uriMatcher.match(uri);
        Cursor theCursor;

        // -- database mode -->

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sMovieId;

        // <-- database mode

        switch (i){
            case SET_MOVIE_ID_FAVORITE:
                int j = db.update(Movie.MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return j;
            default:
                return 0;
        }

    }
}
