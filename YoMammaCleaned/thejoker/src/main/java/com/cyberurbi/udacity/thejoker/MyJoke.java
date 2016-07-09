package com.cyberurbi.udacity.thejoker;

import com.cyberurbi.udacity.projectfourlib.YoMamma;

/** The object model for the data we are sending through endpoints */
public class MyJoke {

    MyJoke() {
        setData(YoMamma.getYoMommaJoke());
    }

    private String myData;

    public String getData() {
        return myData;
    }

    public void setData(String data) {
        myData = data;
    }
}