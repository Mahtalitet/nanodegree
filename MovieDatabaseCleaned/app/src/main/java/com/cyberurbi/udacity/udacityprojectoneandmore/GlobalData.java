package com.cyberurbi.udacity.udacityprojectoneandmore;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Marcin Gruszecki
 * Storage of global variables
 */

public class GlobalData extends Application {


    protected static Movie[] aMovies;

    protected static void initMovies(int c) {


        aMovies = new Movie[c];
        for (int i = 0; i < c; i++) {
            aMovies[i] = new Movie();
        }

    }


    protected static int setRatings(String[] aRatings) {
        int i;
        if(aRatings != null) {
            for (i = 0; i < aRatings.length; i++) {
                if (aMovies[i] == null) {
                    aMovies[i] = new Movie();
                }
                aMovies[i].setsRating(aRatings[i]);
            }
        } else {
            i = 0;
        }
        return i;
    }

    protected static String[] getRatings() {
        if (aMovies != null) {
            String[] a = new String[aMovies.length];
            int i;
            for (i = 0; i < aMovies.length; i++) {
                a[i] = aMovies[i].getsRating();
            }
            return a;
        } else {
            return null;
        }
    }

    protected static int setTitles(String[] aOriginalTitles) {
        int i;
        if(aOriginalTitles != null){

            for (i = 0; i < aOriginalTitles.length; i++) {
                if (aMovies[i] == null) {
                    aMovies[i] = new Movie();
                }
                aMovies[i].setsOriginalTitle(aOriginalTitles[i]);
            }
            return i;
        } else {
            return 0;
        }

    }

    protected static int setReleaseDates(String[] aReleaseDates) {
        int i;
        if(aReleaseDates != null){
            for (i = 0; i < aReleaseDates.length; i++) {
                if (aMovies[i] == null) {
                    aMovies[i] = new Movie();
                }
                aMovies[i].setsReleaseDate(aReleaseDates[i]);
            }
            return i;
        } else {
            return 0;
        }
    }

    protected static String[] getReleaseDates() {

        if (aMovies != null) {
            String[] a = new String[aMovies.length];
            int i;
            for (i = 0; i < aMovies.length; i++) {
                a[i] = aMovies[i].getsReleaseDate();
            }
            return a;
        } else {
            return null;
        }

    }


    protected static int setOverviews(String[] aOverviews) {
        int i;
        if(aOverviews != null){
            for (i = 0; i < aOverviews.length; i++) {
                if (aMovies[i] == null) {
                    aMovies[i] = new Movie();
                }
                aMovies[i].setsOverview(aOverviews[i]);
            }
            return i;
        } else {
            return 0;
        }


    }

    protected static String[] getOverviews() {
        if (aMovies != null) {
            String[] a = new String[aMovies.length];
            int i;
            for (i = 0; i < aMovies.length; i++) {
                a[i] = aMovies[i].getsOverview();
            }
            return a;
        } else {
            return null;
        }
    }

    protected static String[] getOriginalTitles() {

        if (aMovies != null) {
            String[] a = new String[aMovies.length];
            int i;
            for (i = 0; i < aMovies.length; i++) {
                a[i] = aMovies[i].getsOriginalTitle();
            }
            return a;
        } else {
            return null;
        }

    }

    protected static String[] getIds() {

        if (aMovies != null) {
            String[] a = new String[aMovies.length];
            int i;
            for (i = 0; i < aMovies.length; i++) {
                a[i] = aMovies[i].getsId();
            }
            return a;
        } else {
            return null;
        }
    }

    protected static int setIds(String[] aIds) {
        int i;
        if(aIds != null) {
            for (i = 0; i < aIds.length; i++) {
                if (aMovies[i] == null) {
                    aMovies[i] = new Movie();
                }
                aMovies[i].setsId(aIds[i]);
            }
            return i;
        } else {
            return 0;
        }
    }


    final static String cApiModeSortByPopularity = "SORT_BY_POPULARITY";
    final static String cApiModeSortByRating = "SORT_BY_VOTE_AVG";
    final static String cApiModeShowFavorites = "SHOW_FAVORITES";

    final static String cFavoriteYes = "YES";
    final static String cFavoriteNo = "NO";

    private static String sPathImgSmall;
    private static String sPathImgMedium;
    private static String sPathImgBase;
    private static String sApiMode;


    public static String getsApiMode() {
        return sApiMode;
    }

    protected static void setPathToSmallImages(String s) {
        sPathImgSmall = s;
    }

    protected static String getPathToSmallImages() {
        return sPathImgSmall;
    }

    protected static void setPathToMediumImages(String s) {
        sPathImgMedium = s;
    }

    protected static String getPathToMediumImages() {
        return sPathImgMedium;
    }

    protected static void setPathToBaseImages(String s) {
        sPathImgBase = s;
    }

    protected static String getPathToBaseImages() {
        return sPathImgBase;
    }

    protected static void setApiMode(String s) {
        sApiMode = s;
    }

    protected static String getApiMode() {
        return sApiMode;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        setApiMode(cApiModeSortByPopularity);

    }





    protected boolean isOnline() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isOnline = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        if (!isOnline) {

            Toast t = Toast.makeText(getApplicationContext(), R.string.msg_network_not_available, Toast.LENGTH_SHORT);
            t.show();

        }

        return isOnline;

    }


}
