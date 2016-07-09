package com.cyberurbi.udacity.udacityprojectoneandmore;

/**
 * Project One (Udacity Android Nanodegree)
 * Created by Marcin Gruszecki on 2015-07-20.
 */

import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;



import com.squareup.picasso.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopMoviesFragment extends Fragment {


    // Attention!!!
    // Put as apiKey a valid api key obtained from your theMovieDB account
    // String cApiKey = "For_Tests_Put_Valid_Value_Here";

    static final String cApiKey = "810b2ad3430d59895c40436c5803c17b";


    ImageAdapter imgAdpTopMovies;
    String sImgBaseUrl;
    String sImgAppendedPath;
    String sSmallImgAppendedPath;
    GridView gridView;
    Toast t;

    public TopMoviesFragment() {
        super();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadGridViewData(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        imgAdpTopMovies = new ImageAdapter(this.getActivity());
        gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        gridView.setAdapter(imgAdpTopMovies);

        if(GlobalData.aMovies == null) {

            if(((GlobalData)getActivity().getApplication()).isOnline()) {
                new ApiDataDownloader().execute(GlobalData.cApiModeSortByPopularity);
            } else {
                GlobalData.setApiMode(GlobalData.cApiModeSortByPopularity);
                getOfflineMovieData();
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent showDetails = new Intent(rootView.getContext(),Detail.class).putExtra(Intent.EXTRA_TEXT,GlobalData.aMovies[position].getsOriginalTitle());
                showDetails.putExtra("GRID_VIEW_POSITION",position);



                startActivity(showDetails);

            }
        });

    // Menu inflating -->

        this.setHasOptionsMenu(true);
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_top_movies, menu);

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if(GlobalData.getApiMode().equals(GlobalData.cApiModeSortByPopularity)){
            if(actionBar != null){
                actionBar.setSubtitle(R.string.action_bar_title_most_popular);
            }
        } else if (GlobalData.getApiMode().equals((GlobalData.cApiModeSortByRating))) {
            if(actionBar != null){
                actionBar.setSubtitle(R.string.action_bar_title_top_rated);
            }
        } else if (GlobalData.getApiMode().equals(GlobalData.cApiModeShowFavorites)){
            if(actionBar != null){
                actionBar.setSubtitle(R.string.action_favorites);
            }
        }


    }

    private class ApiDataDownloader extends AsyncTask<String,Integer,String[]> {



        String sJson = null;
        String sLogTag = ApiDataDownloader.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            // Get movie data via theMovieDB API --->

            if(params == null)
            {
                return null;
            } else {
                GlobalData.setApiMode(params[0]);
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try {

                URL url = new URL("http://api.themoviedb.org/3/configuration?api_key=" + cApiKey);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line)
                          .append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                sJson = buffer.toString();

                // --> ParseApiConfiguration.Start

                TopMoviesJSONParser jsonConfigurationCall = new TopMoviesJSONParser(sJson);
                JSONObject jsonImages = jsonConfigurationCall.jsonObj.getJSONObject("images");
                sImgBaseUrl = jsonImages.getString("base_url");


                JSONArray aPosterSizes = jsonImages.getJSONArray("poster_sizes");
                String[] aPosterWidthStr = new String[aPosterSizes.length()];

                for(int i=0;i<aPosterSizes.length();i++){

                    aPosterWidthStr[i] = aPosterSizes.getString(i);

                    if(aPosterWidthStr[i].substring(0,1).equals("w")){
                        try{
                            int w = Integer.decode(aPosterWidthStr[i].substring(1));
                            if (w >= 100 && w <= 200){
                                sImgAppendedPath = aPosterWidthStr[i];
                            }
                            if(w <= 160){
                                sSmallImgAppendedPath = aPosterWidthStr[i];
                            }

                        } catch (NumberFormatException e){
                            Log.v(this.getClass().getSimpleName(),"Error processing available poster dimensions for: /'" + aPosterWidthStr[i] + "/'" );
                        }



                    }


                }

                GlobalData.setPathToMediumImages(sImgAppendedPath);
                GlobalData.setPathToSmallImages(sSmallImgAppendedPath);
                GlobalData.setPathToBaseImages(sImgBaseUrl);

                String sProtocol = "http";
                String sHost = "api.themoviedb.org";
                String sApiFunction = "3/discover/movie";
                String sQueryParameter;
                String sQueryKey = "sort_by";
                String sApiKey = "api_key";

                urlConnection.disconnect();

                // <-- ParseApiConfiguration.End

                Uri.Builder UriBuilder = new Uri.Builder();
                UriBuilder.scheme(sProtocol);
                UriBuilder.authority(sHost);
                UriBuilder.appendEncodedPath(sApiFunction);

                if(GlobalData.getApiMode().equals(GlobalData.cApiModeSortByPopularity)){
                    sQueryParameter = "popularity.desc";
                } else {
                    UriBuilder.appendQueryParameter("vote_count.gte","50");
                    sQueryParameter = "vote_average.desc";
                }

                UriBuilder.appendQueryParameter(sQueryKey,sQueryParameter);
                UriBuilder.appendQueryParameter(sApiKey,cApiKey);
                UriBuilder.build();

                URL topMoviesUrl = new URL(UriBuilder.build().toString());

                //# simplify ... -->

                urlConnection = (HttpURLConnection) topMoviesUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    buffer.append(line)
                          .append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                sJson = buffer.toString();

    // ParseDiscoverOutputJsonData.Start

                TopMoviesJSONParser jsonDiscoveryCall = new TopMoviesJSONParser(sJson);
                JSONArray jsonMoviesArray = jsonDiscoveryCall.jsonObj.getJSONArray("results");

                if(GlobalData.aMovies != null)
                {
                    GlobalData.aMovies = null;
                }

                GlobalData.initMovies(jsonMoviesArray.length());

                for(int i=0;i<jsonMoviesArray.length();i++)
                {
                    GlobalData.aMovies[i].setsOriginalTitle(jsonMoviesArray.getJSONObject(i).getString("original_title"));
                    GlobalData.aMovies[i].setsPosterPath(jsonMoviesArray.getJSONObject(i).getString("poster_path"));
                    GlobalData.aMovies[i].setsOverview(jsonMoviesArray.getJSONObject(i).getString("overview"));
                    GlobalData.aMovies[i].setsReleaseDate(jsonMoviesArray.getJSONObject(i).getString("release_date"));
                    GlobalData.aMovies[i].setsRating(jsonMoviesArray.getJSONObject(i).getString("vote_average"));
                    GlobalData.aMovies[i].setsId(jsonMoviesArray.getJSONObject(i).getString("id"));
                    GlobalData.aMovies[i].setPopularity((float) jsonMoviesArray.getJSONObject(i).getDouble("popularity"));

                    Cursor c = getActivity().getContentResolver().query(Movie.MovieEntry.buildIsFavouriteMovieById(GlobalData.aMovies[i].getsId()),null,null,null,null);

                    if(c.getCount() > 0){
                        c.moveToPosition(0);
                        GlobalData.aMovies[i].setFavorite(c.getString(c.getColumnIndex(Movie.MovieEntry.COLUMN_FAVORITE)).equals(GlobalData.cFavoriteYes));

                    } else {
                        GlobalData.aMovies[i].setFavorite(false);
                    }

                    getActivity().getContentResolver().insert(Movie.MovieEntry.CONTENT_URI, GlobalData.aMovies[i].getMovieEntry());
                }

    // ParseDiscoverOutputJsonData.End

            } catch (UnknownHostException e) {

                Log.e(sLogTag, "Network not available ", e);
                if(t != null)
                {
                    t.cancel();
                }
                t = Toast.makeText(getActivity(),"Network not available",Toast.LENGTH_LONG);
                t.show();


            } catch (IOException e) {

                Log.e(sLogTag, "I/O Error ", e);
                return null;

            } catch (JSONException e) {

                Log.e(sLogTag, "Parsing Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(sLogTag, "Error closing stream", e);
                    }
                }

            }

            return GlobalData.getOriginalTitles();
        }


        protected void onPostExecute(String[] result) {

            if(result != null) {
                imgAdpTopMovies.notifyDataSetChanged();
            }
        }

    }

    private class TopMoviesJSONParser {

        JSONObject jsonObj;

        public TopMoviesJSONParser(String sJSON){

            try {
                jsonObj = new JSONObject(sJSON);
            } catch (JSONException e) {
                Log.e(TopMoviesJSONParser.class.getSimpleName(),e.getMessage());
            }

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();

        if(id == R.id.action_sort_by_popularity)
            {

                GlobalData.setApiMode(GlobalData.cApiModeSortByPopularity);
                if(actionBar != null){
                    actionBar.setSubtitle(R.string.action_bar_title_most_popular);
                }

                if(((GlobalData)getActivity().getApplication()).isOnline()) {
                    new ApiDataDownloader().execute(GlobalData.cApiModeSortByPopularity);
                } else {
                    getOfflineMovieData();
                }

                return true;
            }

        if(id == R.id.action_best_rating)
            {
                GlobalData.setApiMode(GlobalData.cApiModeSortByRating);
                if(actionBar != null){
                    actionBar.setSubtitle(R.string.action_bar_title_top_rated);
                }

                if(((GlobalData)getActivity().getApplication()).isOnline()) {
                    new ApiDataDownloader().execute(GlobalData.cApiModeSortByRating);
                } else {
                    getOfflineMovieData();
                }

                return true;
            }

        if(id == R.id.action_show_favorites)
            {
                GlobalData.setApiMode(GlobalData.cApiModeShowFavorites);
                if(actionBar != null){
                    actionBar.setSubtitle(R.string.action_favorites);
                }
                getOfflineMovieData();
                return true;
            }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        this.storeGridViewData(outState);

    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
        loadGridViewData(savedInstanceState);

    }


    private void storeGridViewData(@Nullable Bundle outState){

        if(outState != null) {

            String[] storedTopMovies = new String[imgAdpTopMovies.getCount()];
            for (int i = 0; i < imgAdpTopMovies.getCount(); i++) {
                storedTopMovies[i] = (String) imgAdpTopMovies.getItem(i);
            }

            outState.putStringArray("topMovies", storedTopMovies);
            outState.putStringArray("ratings", GlobalData.getRatings());
            outState.putStringArray("overviews", GlobalData.getOverviews());
            outState.putStringArray("releaseDates", GlobalData.getReleaseDates());
            outState.putStringArray("ids",GlobalData.getIds());
            outState.putString("subtitle", GlobalData.getApiMode());

        }
    }

    protected static void loadGridViewData(@Nullable Bundle savedInstanceState){

        if(savedInstanceState != null){

            String sOriginalTitle;
            String sPosterPath;

            String[] aSavedState = savedInstanceState.getStringArray("topMovies");

            if(aSavedState != null) {
                for (int i = 0; i < aSavedState.length; i++) {

                    String s = aSavedState[i];
                    sOriginalTitle = s.substring(0, s.indexOf("|"));
                    sPosterPath = s.substring(s.indexOf("|") + 1);

                    GlobalData.aMovies[i].setsOriginalTitle(sOriginalTitle);
                    GlobalData.aMovies[i].setsPosterPath(sPosterPath);
                }

                GlobalData.setRatings(savedInstanceState.getStringArray("ratings"));
                GlobalData.setReleaseDates(savedInstanceState.getStringArray("releaseDates"));
                GlobalData.setOverviews(savedInstanceState.getStringArray("overviews"));
                GlobalData.setIds(savedInstanceState.getStringArray("ids"));
                GlobalData.setApiMode(savedInstanceState.getString("subtitle"));
            }

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        loadGridViewData(savedInstanceState);

    }

    private class ImageAdapter extends BaseAdapter {

        private Context theContext;

        ImageAdapter(Context c){
            theContext = c;
        }

        @Override
        public int getCount() {
            if(GlobalData.aMovies != null) {
                return GlobalData.aMovies.length;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {

            return GlobalData.aMovies[position].getsOriginalTitle() + "|" + GlobalData.aMovies[position].getsPosterPath();

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            View grid;
            LayoutInflater inflater = (LayoutInflater) theContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(sImgBaseUrl == null){
                sImgBaseUrl = "http://image.tmdb.org/t/p/";
            }


            if (convertView == null) {
                grid = inflater.inflate(R.layout.movie_list_item, null);
                grid.setId(position);
                grid.findViewById(R.id.movie_grid_image_view).setBackgroundResource(R.drawable.movie_filler);
            } else {
                grid = convertView;
            }

            TextView txtView = (TextView) grid.findViewById(R.id.movie_grid_text_view);
            txtView.setText(GlobalData.aMovies[position].getsOriginalTitle());

            final ImageView imgView = (ImageView)grid.findViewById(R.id.movie_grid_image_view);
            if(sImgAppendedPath == "" || sImgAppendedPath == null){
                sImgAppendedPath = "w185";
            }

            imgView.setAdjustViewBounds(true);
            imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Check if file stored ...
            String sLocalPath = theContext.getFilesDir() + GlobalData.aMovies[position].getsPosterPath();
            File f = new File(sLocalPath);

            if(f.exists())
            {

                imgView.setImageURI(Uri.fromFile(f));

            } else {
                // ... if not

                final String sUrl = sImgBaseUrl + sImgAppendedPath + GlobalData.aMovies[position].getsPosterPath();

                Picasso.with(theContext).load(sUrl).placeholder(R.drawable.movie_filler).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        try {

                            imgView.setImageBitmap(bitmap);
                            String s = GlobalData.aMovies[position].getsPosterPath();
                            FileOutputStream fOut = theContext.openFileOutput(s.substring(1), 0);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.close();

                            Log.v("#0908", "Object stored - " + s);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        // ... do nothing ...
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imgView.setImageDrawable(placeHolderDrawable);
                    }
                });

            }

            return grid;
        }


    }

    private void getOfflineMovieData(){

        int rowsCount;
        String sOrderByClause;
        String sWhereClause = null;
        String[] sWhereClauseParams = null;

        switch (GlobalData.getApiMode()){
            case GlobalData.cApiModeSortByPopularity:   sOrderByClause = Movie.MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20";
                                                        break;
            case GlobalData.cApiModeSortByRating:       sOrderByClause = Movie.MovieEntry.COLUMN_RATING + " DESC LIMIT 20";
                                                        break;
            case GlobalData.cApiModeShowFavorites:      sOrderByClause = Movie.MovieEntry.COLUMN_TITLE + " ASC";
                                                        sWhereClause = Movie.MovieEntry.COLUMN_FAVORITE + " = ?";
                                                        sWhereClauseParams = new String[]{GlobalData.cFavoriteYes};
                                                        break;
            default: sOrderByClause = null;

        }


        Cursor dataSet = getActivity().getApplicationContext().getContentResolver().query(Movie.MovieEntry.CONTENT_URI,null,sWhereClause,sWhereClauseParams,sOrderByClause);
        rowsCount = dataSet.getCount();

        if(rowsCount > 0){

            GlobalData.initMovies(rowsCount);
            for(int i=0;i<rowsCount;i++){

                dataSet.moveToPosition(i);
                GlobalData.aMovies[i].setsId(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_ID)));
                GlobalData.aMovies[i].setsOriginalTitle(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_TITLE)));
                GlobalData.aMovies[i].setsPosterPath(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_POSTER_PATH)));
                GlobalData.aMovies[i].setsReleaseDate(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_RELEASE_DATE)));
                GlobalData.aMovies[i].setsRating(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_RATING)));
                GlobalData.aMovies[i].setsOverview(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_OVERVIEW)));
                GlobalData.aMovies[i].setPopularity(dataSet.getFloat(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_POPULARITY)));
                if(dataSet.getString(dataSet.getColumnIndex(Movie.MovieEntry.COLUMN_FAVORITE)).equals(GlobalData.cFavoriteYes)){
                    GlobalData.aMovies[i].setFavorite(true);
                } else {
                    GlobalData.aMovies[i].setFavorite(false);
                }
            }
            imgAdpTopMovies.notifyDataSetChanged();

        } else {

            if(GlobalData.getApiMode().equals(GlobalData.cApiModeShowFavorites)){

                if(t != null){
                    t.cancel();
                }
                t = Toast.makeText(getActivity().getApplicationContext(),R.string.msg_no_movie_found_in_favorites,Toast.LENGTH_SHORT);
                t.show();
            }

        }
        dataSet.close();

    }




}