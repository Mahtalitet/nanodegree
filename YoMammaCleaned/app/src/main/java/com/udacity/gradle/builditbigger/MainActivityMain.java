package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;


public class MainActivityMain extends ActionBarActivity {


    static String theJoke;
    //final private String theJokeKey = "JOKE_KEY";
    static ProgressBar spinner;





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

        MainActivityMain.theJoke = "";
        new EndpointsAsyncTaskMain().execute(new Pair<Context, String>(this, "Meyer"));
        spinner.setVisibility(View.VISIBLE);

    }


}
