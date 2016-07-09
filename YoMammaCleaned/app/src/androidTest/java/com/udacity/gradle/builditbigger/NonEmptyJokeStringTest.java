package com.udacity.gradle.builditbigger;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.test.ApplicationTestCase;
import android.test.AndroidTestCase;

import com.cyberurbi.udacity.project4androidlibrary.P4AndroidMainActivity;
import com.cyberurbi.udacity.projectfourlib.YoMamma;

import junit.framework.TestCase;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * Created by MG on 2015-11-14.
 */
public class NonEmptyJokeStringTest extends AndroidTestCase {


    public Object testLock;

    public NonEmptyJokeStringTest() {
        super();
        verifyNonEmptyJokeString();
    }

    public void verifyNonEmptyJokeString(){

        Joker j = new Joker();
        j.execute();


        Object objLock = new Object();


        synchronized (objLock) {
            try {
                objLock.wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertNotNull("Error #1: No joke told", j.theJoke);


    }

    class Joker extends AsyncTask<String,Void,String> {

        protected String theJoke;

        protected String doInBackground(String... urls) {
            String s = YoMamma.getYoMommaJoke();
            theJoke = s;
            return s;
        }

        protected void onPostExecute(String pResult) {
            theJoke = pResult;
        }

    }


}
