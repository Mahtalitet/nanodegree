package com.cyberurbi.udacity.projectfourlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


public class YoMamma  {

    private static String sYomommaUrl= "http://api.yomomma.info/";

    public static String getStringAtUrl(String pUrl){

        URL sourceHost = null;
        HttpURLConnection theConnection;
        try {
            sourceHost = new URL(pUrl);
            theConnection = (HttpURLConnection) sourceHost.openConnection();
            theConnection.setRequestMethod("GET");
            theConnection.connect();

            InputStream inputStream = theConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }

            theConnection.disconnect();
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getYoMommaJoke(){

        try {
            return new JSONObject(getStringAtUrl(sYomommaUrl)).getString("joke");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



}
