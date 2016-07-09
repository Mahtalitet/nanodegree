package com.udacity.gradle.builditbigger.paid;

import android.app.ActionBar;
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


import com.udacity.gradle.builditbigger.R;


import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity {


    static String theJoke;
    static ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paid);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Yo Momma Jokes");
            actionBar.setSubtitle("Paid Version");
        }

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

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
        spinner.setVisibility(View.VISIBLE);

    }


}
