package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService
{
    public static final String LOG_TAG = "myFetchService";
    public myFetchService()
    {
        super("myFetchService");
    }

    private int progressMade = 0;
    private ProgressBar progressBar;
    private TextView progressBarMsg;

    @Override
    protected void onHandleIntent(Intent intent)
    {

        // --- Version 6.0 --->

        Context theContext = MainActivity.theContext;
        SharedPreferences sharedPreferences = theContext.getSharedPreferences(DatabaseContract.CONTENT_AUTHORITY,0);

        if(sharedPreferences != null){
            long then = sharedPreferences.getLong(getString(R.string.last_results_download_timestamp),0);
            long now = new Date().getTime();

            if (now - then <  3600000){
                // return;
            }
        }


        final String pSearchPastDays = "p" + String.valueOf(MainActivity.current_fragment);
        final String pSearchFutureDays = "n" + String.valueOf(MainActivity.current_fragment);

        // <--- Version 6.0 ---


        getData(pSearchFutureDays);
        getData(pSearchPastDays);


        // --- Version 6.0 --->

        // Set last update time
        long nowFinal = new Date().getTime();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(getString(R.string.last_results_download_timestamp), nowFinal);
        editor.commit();

        // <--- Version 6.0 ---




        return;
    }

    private void getData (String timeFrame)
    {
        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            // TODO Remove key
            String sApkiKey = "";

            // String sApiKey = "e136b7858d424b9da07c88f28b61989a";
            m_connection.addRequestProperty("X-Auth-Token", sApiKey);
            m_connection.connect();

            // -- Delay due to API limitiation 50 calls / minute -->
            Thread.sleep(1200);



            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.

                    // --- Version 6.0 --->
                       //  processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    // <--- Version 6.0 ---



                    return;
                }

                // -- progressBar = (ProgressBar)MainActivity.mainPagerFragment.getView().findViewById(R.id.progress_bar);
                // -- Double update due to Progess Bar issue -->
                // progressBar.setMax(0);
                // progressBar.setMax(matches.length());
                // progressBar.setProgress(progressMade);
                // -- progressBar.setVisibility(ProgressBar.VISIBLE);
                // -- progressBar.setIndeterminate(true);

                // -- progressBarMsg = (TextView) MainActivity.mainPagerFragment.getView().findViewById(R.id.progress_bar_msg);
                // -- progressBarMsg.setText(R.string.progress_bar_msg);
                // -- progressBarMsg.setVisibility(TextView.VISIBLE);

                ListView listView = (ListView)MainActivity.mainPagerFragment.getView().findViewById(R.id.scores_list);
                listView.setAlpha((float)0.1);

                processJSONdata(JSON_data, getApplicationContext(), true);

                // -- progressBar.setVisibility(ProgressBar.INVISIBLE);
                // -- progressBarMsg.setVisibility(TextView.INVISIBLE);
                // -- progressMade = 0;



                listView.setAlpha((float) 1.00);

            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }
    private void processJSONdata (String JSONdata,Context mContext, boolean isReal)
    {
        //JSON data
        final String SERIE_A = String.valueOf(Utilies.SERIE_A);
        final String PREMIER_LEGAUE = String.valueOf(Utilies.PREMIER_LEGAUE);
        final String CHAMPIONS_LEAGUE = String.valueOf(Utilies.CHAMPIONS_LEAGUE);
        final String PRIMERA_DIVISION = String.valueOf(Utilies.PRIMERA_DIVISION);
        final String BUNDESLIGA = String.valueOf(Utilies.BUNDESLIGA);

        // --- Version 6.0 --->

        final String BUNDESLIGA1_15 = String.valueOf(Utilies.BUNDESLIGA1_15);
        final String BUNDESLIGA2_15 = String.valueOf(Utilies.BUNDESLIGA2_15 );
        final String LIGUE1_15 = String.valueOf(Utilies.LIGUE1_15);
        final String LIGUE2_15 = String.valueOf(Utilies.LIGUE2_15);
        final String PREMIERLEAGUE_15 = String.valueOf(Utilies.PREMIERLEAGUE_15);
        final String PRIMERADIVISION_15 = String.valueOf(Utilies.PRIMERADIVISION_15);
        final String SEGUNDADIVISION_15 = String.valueOf(Utilies.SEGUNDADIVISION_15);
        final String SERIEA_15 = String.valueOf(Utilies.SERIEA_15);
        final String PRIMEIRALIGA_15 = String.valueOf(Utilies.PRIMEIRALIGA_15);
        final String BUNDESLIGA3_15 = String.valueOf(Utilies.BUNDESLIGA3_15);
        final String EREDIVISIE_15 = String.valueOf(Utilies.EREDIVISIE_15);
        final String CHAMPIONS_15 = String.valueOf(Utilies.CHAMPIONS_15);

        // <--- Version 6.0 ---

        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        // --- Version 6.0 --->

        final String TEAM_LINK = "http://api.football-data.org/alpha/teams/";
        final String HOME_TEAM_LINK = "homeTeam";
        final String AWAY_TEAM_LINK = "awayTeam";

        // <--- Version 6.0 ---

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;

        // --- Version 6.0 --->

        String home_team_link = null;
        String away_team_link = null;
        String home_crest_link = null;
        String away_crest_link = null;
        int home_team_id = 0;
        int away_team_id = 0;

        // <--- Version 6.0 ---



        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++)
            {
                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");
                League = League.replace(SEASON_LINK,"");
                if(     League.equals(PREMIER_LEGAUE)      ||
                        League.equals(SERIE_A)             ||
                        League.equals(CHAMPIONS_LEAGUE)    ||
                        League.equals(BUNDESLIGA)          ||

                        // --- Version 6.0 --->

                        League.equals(BUNDESLIGA1_15)       ||
                        League.equals(BUNDESLIGA2_15)       ||
                        League.equals(BUNDESLIGA3_15)       ||
                        League.equals(LIGUE1_15)            ||
                        League.equals(LIGUE2_15)            ||
                        League.equals(PREMIERLEAGUE_15)     ||
                        League.equals(PRIMERADIVISION_15)   ||
                        League.equals(SEGUNDADIVISION_15)   ||
                        League.equals(PRIMEIRALIGA_15)      ||
                        League.equals(SERIEA_15)            ||
                        League.equals(EREDIVISIE_15)        ||
                        League.equals(CHAMPIONS_15)         ||

                        // <--- Version 6.0 ---

                        League.equals(PRIMERA_DIVISION)     )
                {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString("href");
                    match_id = match_id.replace(MATCH_LINK, "");
                    if(!isReal){
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id=match_id+Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0,mDate.indexOf("T"));
                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(mDate+mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0,mDate.indexOf(":"));

                        if(!isReal){
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis()+((i-MainActivity.current_fragment)*86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG,e.getMessage());
                    }
                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);

                    // --- Version 6.0 --->
                    // Get crest links



                    home_team_link = match_data.getJSONObject(LINKS).getJSONObject(HOME_TEAM_LINK).getString("href");
                    away_team_link = match_data.getJSONObject(LINKS).getJSONObject(AWAY_TEAM_LINK).getString("href");

                    home_team_id = Integer.parseInt(home_team_link.replace(TEAM_LINK, ""));
                    away_team_id = Integer.parseInt(away_team_link.replace(TEAM_LINK, ""));

                    Cursor c = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithHomeTeamId(),new String[]{DatabaseContract.scores_table.HOME_CREST_LINK},null,new String[]{String.valueOf(home_team_id)},null);
                    if(c.getCount() > 0){
                        c.moveToFirst();
                        home_crest_link = c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_CREST_LINK));
                        c.close();
                    } else {
                        c.close();
                        c = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithAwayTeamId(),new String[]{DatabaseContract.scores_table.AWAY_CREST_LINK},null,new String[]{String.valueOf(home_team_id)},null);
                        if(c.getCount()>0){
                            c.moveToFirst();
                            home_crest_link = c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_CREST_LINK));
                            c.close();
                        } else {
                            c.close();
                            home_crest_link = getCrestLinkFromTeamLink(home_team_link);
                        }
                    }



                    c = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithHomeTeamId(),new String[]{DatabaseContract.scores_table.HOME_CREST_LINK},null,new String[]{String.valueOf(away_team_id)},null);
                    if(c.getCount() > 0){
                        c.moveToFirst();
                        away_crest_link = c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_CREST_LINK));
                        c.close();
                    } else {
                        c.close();
                        c = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithAwayTeamId(),new String[]{DatabaseContract.scores_table.AWAY_CREST_LINK},null,new String[]{String.valueOf(away_team_id)},null);
                        if(c.getCount()>0){
                            c.moveToFirst();
                            away_crest_link = c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_CREST_LINK));
                            c.close();
                        } else {
                            c.close();
                            away_crest_link = getCrestLinkFromTeamLink(away_team_link);
                        }
                    }


                    // <--- Version 6.0 ---

                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID,match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL,mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL,mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL,Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL,Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL,Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL,Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL,League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY,match_day);

                    // --- Version 6.0 --->

                    match_values.put(DatabaseContract.scores_table.HOME_CREST_LINK, home_crest_link);
                    match_values.put(DatabaseContract.scores_table.AWAY_CREST_LINK, away_crest_link);
                    match_values.put(DatabaseContract.scores_table.HOME_TEAM_ID, home_team_id);
                    match_values.put(DatabaseContract.scores_table.AWAY_TEAM_ID, away_team_id);

                    // <--- Version 6.0 ---

                    //log spam

                    //Log.v(LOG_TAG,match_id);
                    //Log.v(LOG_TAG,mDate);
                    //Log.v(LOG_TAG,mTime);
                    //Log.v(LOG_TAG,Home);
                    //Log.v(LOG_TAG,Away);
                    //Log.v(LOG_TAG,Home_goals);
                    //Log.v(LOG_TAG,Away_goals);

                    values.add(match_values);

                    progressMade++;
                    // progressBar.setProgress(0);
                    // progressBar.setProgress(progressMade);
                    String sProgressBarMsg = mContext.getString(R.string.progress_bar_msg) + " " + progressMade + " " + mContext.getString(R.string.progress_bar_msg_part2) + " " + progressBar.getMax();
                    progressBarMsg.setText(sProgressBarMsg);




                }
            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI,insert_data);



            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    private String getCrestLinkFromTeamLink(String teamLink){

        final String AWAY_TEAM_LINK = getString(R.string.crest_url);

        try {

            URL teamUrl = new URL(teamLink);
            HttpURLConnection teamConnection = (HttpURLConnection) teamUrl.openConnection();
            teamConnection.setRequestMethod(getString(R.string.request_format_get));
            teamConnection.addRequestProperty(getString(R.string.request_auth_token_x), getString(R.string.api_key));
            teamConnection.connect();

            // -- Delay due to API limitiation 50 calls / minute -->
            Thread.sleep(1200);

            InputStream teamDataPipe = teamConnection.getInputStream();
            StringBuffer message = new StringBuffer();

            if(teamDataPipe == null){
                return null;
            }

            BufferedReader teamDataReader = new BufferedReader(new InputStreamReader(teamDataPipe));

            String messageLine = teamDataReader.readLine();
            while(messageLine != null){
                message.append(messageLine);
                messageLine = teamDataReader.readLine();
            }

            if(message.length() == 0){
                return null;
            }

            JSONObject jsonTeamData = new JSONObject(message.toString());
            return jsonTeamData.getString(AWAY_TEAM_LINK);


        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (JSONException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

}

