package com.udacity.gradle.builditbigger.free;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cyberurbi.udacity.thejoker.myApi.*;

import com.cyberurbi.udacity.project4androidlibrary.P4AndroidMainActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by MG on 2015-11-28.
 */


class EndpointsAsyncTask extends AsyncTask <Pair<Context, String>, Void, String> {

    private static MyApi jokeApiService = null;
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, String>... params) {

        if(jokeApiService == null) {
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl("https://udacity-project-4-1181.appspot.com/_ah/api/");

            jokeApiService = builder.build();
        }

        context = params[0].first;
        String name = params[0].second;

        try {
            return jokeApiService.tellJoke(name).execute().getData();
        }
        catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Intent myIntent = new Intent(context, P4AndroidMainActivity.class);
        myIntent.putExtra(P4AndroidMainActivity.theJokeKey, result);
        MainActivity.spinner.setVisibility(ProgressBar.INVISIBLE);
        context.startActivity(myIntent);
    }

}