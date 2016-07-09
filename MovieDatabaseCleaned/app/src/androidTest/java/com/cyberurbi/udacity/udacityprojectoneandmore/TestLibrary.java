package com.cyberurbi.udacity.udacityprojectoneandmore;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created for test purposes
 * by Marcin Gruszecki
 */
public class TestLibrary extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        getContext().deleteDatabase(MoviesDbHelper.DATABASE_NAME);

    }

    public void testOpenDatabase() throws Exception {

        MoviesDbHelper SQLiteHelper = new MoviesDbHelper(getContext());
//        assertTrue("Case 01 - Database created",(SQLiteHelper != null));

        SQLiteDatabase db = SQLiteHelper.getWritableDatabase();
        assertTrue("Case 02 - Database accessed", (db != null));



        assertTrue("Case 03 - Data inserted", (db.insert(Movie.MovieEntry.TABLE_NAME, null, createGodFatherMovieData()) != -1));

        Cursor dataSet = db.query(Movie.MovieEntry.TABLE_NAME, new String[]{Movie.MovieEntry.COLUMN_TITLE},null,null,null,null,null);
        assertTrue("Case 04 - Data read", (dataSet.getCount() > 0 && dataSet.moveToFirst()));

        assertTrue("Case 05 - Data found at position " + dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_TITLE),(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_TITLE) != -1));

        assertEquals("Case 06 - Data written equal to data read",(createGodFatherMovieData().get(Movie.MovieEntry.COLUMN_TITLE)).toString(),dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_TITLE)));

        dataSet.close();
        db.close();
    }


    static ContentValues createGodFatherMovieData() {

        ContentValues testValues = new ContentValues();
        testValues.put(Movie.MovieEntry.COLUMN_ID,"168259");
        testValues.put(Movie.MovieEntry.COLUMN_TITLE,"Furious 7");
        testValues.put(Movie.MovieEntry.COLUMN_POSTER_PATH,"/dCgm7efXDmiABSdWDHBDBx2jwmn.jpg");
        testValues.put(Movie.MovieEntry.COLUMN_OVERVIEW,"Dominic and his crew thought they left the criminal mercenary life behind. They defeated an international terrorist named Owen Shaw and went their seperate ways. But now, Shaw's brother, Deckard Shaw is out killing the crew one by one for revenge. Worse, a Somalian terrorist called Jakarde, and a shady government official called \\\"Mr. Nobody\\\" are both competing to steal a computer terrorism program called God's Eye, that can turn any technological device into a weapon. Torretto must reconvene with his team to stop Shaw and retrieve the God's Eye program while caught in a power struggle between terrorist and the United States government");
        testValues.put(Movie.MovieEntry.COLUMN_RELEASE_DATE,"2015-04-03");
        testValues.put(Movie.MovieEntry.COLUMN_RATING,"7.6");
        testValues.put(Movie.MovieEntry.COLUMN_POPULARITY,33.3);
        testValues.put(Movie.MovieEntry.COLUMN_FAVORITE,GlobalData.cFavoriteYes);

        return testValues;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
