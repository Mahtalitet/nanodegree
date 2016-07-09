package com.cyberurbi.udacity.udacityprojectoneandmore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marcin Gruszecki
 * for ProjectTwo
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 10;
    final static String DATABASE_NAME = "PopularMovies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String Create_Movies_Table = "CREATE TABLE "+ Movie.MovieEntry.TABLE_NAME +"(" +
                Movie.MovieEntry.COLUMN_ID + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_RATING + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_FAVORITE + " TEXT NOT NULL," +
                Movie.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                " UNIQUE (" + Movie.MovieEntry.COLUMN_ID + ") ON CONFLICT REPLACE)";


        final String Create_Trailers_Table = "CREATE TABLE " + Movie.TrailerEntry.TABLE_NAME + "(" +
                Movie.TrailerEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                Movie.TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL," +
                Movie.TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL," +
                " FOREIGN KEY (" + Movie.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " + Movie.MovieEntry.TABLE_NAME + "(" + Movie.MovieEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                " UNIQUE (" + Movie.TrailerEntry.COLUMN_MOVIE_ID + "," + Movie.TrailerEntry.COLUMN_YOUTUBE_KEY  + ") ON CONFLICT REPLACE)";


        final String Create_Reviews_Table = "CREATE TABLE " + Movie.ReviewEntry.TABLE_NAME  + "(" +
                Movie.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                Movie.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                Movie.ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL," +
                " FOREIGN KEY (" + Movie.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " + Movie.MovieEntry.TABLE_NAME + "(" + Movie.MovieEntry.COLUMN_ID + ") ON DELETE CASCADE," +
                " UNIQUE (" + Movie.ReviewEntry.COLUMN_MOVIE_ID + "," + Movie.ReviewEntry.COLUMN_AUTHOR+ ") ON CONFLICT REPLACE)";

        db.execSQL(Create_Movies_Table);
        db.execSQL(Create_Trailers_Table);
        db.execSQL(Create_Reviews_Table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Movie.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Movie.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Movie.ReviewEntry.TABLE_NAME);
        onCreate(db);

    }
}
