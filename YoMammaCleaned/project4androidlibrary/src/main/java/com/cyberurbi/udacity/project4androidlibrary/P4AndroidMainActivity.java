package com.cyberurbi.udacity.project4androidlibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class P4AndroidMainActivity extends AppCompatActivity {

    final public static String theJokeKey = "JOKE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p4_android_main);

        Intent startingIntent = getIntent();
        TextView jokeText =  (TextView)findViewById(R.id.jokeText);
        jokeText.setText(startingIntent.getStringExtra(theJokeKey));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
