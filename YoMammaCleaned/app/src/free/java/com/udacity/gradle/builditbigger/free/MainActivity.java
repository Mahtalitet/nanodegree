package com.udacity.gradle.builditbigger.free;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cyberurbi.udacity.projectfourlib.YoMamma;
import com.cyberurbi.udacity.project4androidlibrary.P4AndroidMainActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.udacity.gradle.builditbigger.R;


import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity {


    static String theJoke;
    //final private String theJokeKey = "JOKE_KEY";
    static ProgressBar spinner;
    static InterstitialAd fullPageAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_free);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Yo Momma Jokes");
            actionBar.setSubtitle("Free Version");
        }

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);


        fullPageAd = new InterstitialAd(this);
        fullPageAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        fullPageAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

    }

    private void requestNewInterstitial(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        fullPageAd.loadAd(adRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view){

        MainActivity.theJoke = "";
        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Meyer"));
        if (fullPageAd.isLoaded()) {
            fullPageAd.show();
        } else {
            spinner.setVisibility(View.VISIBLE);
        }

    }


}
