package com.cyberurbi.udacity.udacityprojectoneandmore;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
// import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.*;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Detail extends ActionBarActivity {

    ArrayList<String> aYouTubeKeys;
    ArrayList<String> aYouTubeTitles;
    ArrayAdapter<String> trailerListAdapter;
    ListView trailerListView;

    ArrayList<String> aReviewAuthors;
    ArrayList<String> aReviews;
    ReviewAdapter reviewListAdapter;
    ListView reviewListView;

    int currentMoviePosition;
    Menu currentMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment(), "detailFragment")
                        .commit();
            }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail, menu);
        currentMenu = menu;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("");
        actionBar.setTitle("");

        TextView textViewOriginalTitle = (TextView)findViewById(R.id.original_title_text_view);
        textViewOriginalTitle.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));

        final int i = getIntent().getIntExtra("GRID_VIEW_POSITION",0);
        currentMoviePosition = i;

        MenuItem menuItem = menu.findItem(R.id.action_make_favorite);
        if(GlobalData.aMovies[i].isFavorite()){
            menuItem.setIcon(R.drawable.ic_action_favorites);
        } else {
            menuItem.setIcon(R.drawable.ic_action_make_favourite);
        }

        if(Build.VERSION.SDK_INT >= 14) {

    /*        ShareActionProvider trailerShareActionProvider;

            MenuItem menuShareItem = menu.findItem(R.id.action_share_trailer);
            trailerShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuShareItem);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Trailer (" + GlobalData.aMovies[currentMoviePosition].getsOriginalTitle() +")");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Have fun! See here:" + "http://www.youtube.com/watch?v=RFinNxS5KN4");
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=RFinNxS5KN4");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            trailerShareActionProvider.setShareIntent(shareIntent);*/

        }

        String sReleaseDate = GlobalData.aMovies[i].getsReleaseDate();
        TextView releaseDate = (TextView)this.findViewById(R.id.release_date_view);
        releaseDate.setText(sReleaseDate);

        String sRating = GlobalData.aMovies[i].getsRating() + "/10";
        TextView ratingView = (TextView)this.findViewById(R.id.rating_view);
        ratingView.setText(sRating);

        String sOverview = GlobalData.aMovies[i].getsOverview();
        TextView txtView = (TextView)this.findViewById(R.id.story_view);
        txtView.setText(sOverview);

        final ImageView imgView = (ImageView)findViewById(R.id.detail_poster_view);
        final String sUrl  = GlobalData.getPathToBaseImages() + GlobalData.getPathToSmallImages() + GlobalData.aMovies[i].getsPosterPath();
        imgView.setAdjustViewBounds(true);
        imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgView.setImageResource(R.drawable.movie_filler);

    // Check if file stored ...

        String sLocalPath = getApplicationContext().getFilesDir() + GlobalData.aMovies[i].getsPosterPath();
        File f = new File(sLocalPath);

        if(f.exists())
        {

            imgView.setImageURI(Uri.fromFile(f));

        } else {

    //  ... else - download the poster.

            Picasso.with(getApplicationContext()).load(sUrl).placeholder(R.drawable.movie_filler).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {

                        imgView.setImageBitmap(bitmap);
                        String s = GlobalData.aMovies[i].getsPosterPath();
                        FileOutputStream fOut = getApplicationContext().openFileOutput(s.substring(1), 0);
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

        // Call API to get trailer list -->

        trailerListView = (ListView) findViewById(R.id.trailer_list_view);

        if(((GlobalData)getApplication()).isOnline()) {
            new ApiTrailerDataDownloader().execute(String.valueOf(i));
        } else {
            getOfflineTrailerData(GlobalData.aMovies[i].getsId());
        }
        // <-- Trailer list

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sYouTubeUrl = "http://www.youtube.com/watch?v=" + aYouTubeKeys.get(position);
                Intent showYouTubeVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(sYouTubeUrl));
                startActivity(showYouTubeVideo);
            }
        });

        // Call API to get reviews list -->

        reviewListView = (ListView) findViewById(R.id.reviews_list_view);


        if(((GlobalData)getApplication()).isOnline()) {
            new ApiReviewDataDownloader().execute(String.valueOf(i));
        } else {
            getOfflineReviewData(GlobalData.aMovies[i].getsId());
        }

        // <-- Reviews list

        ScrollView detailScrollView = (ScrollView)findViewById(R.id.detail_scroll_view);
        detailScrollView.scrollTo(0,0);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_make_favorite)
        {
            if(GlobalData.aMovies[currentMoviePosition].isFavorite()){
                // 1. mark it on UI
                item.setIcon(R.drawable.ic_action_make_favourite);
                // 2. mark it in memory
                GlobalData.aMovies[currentMoviePosition].setFavorite(false);
            } else {
                // 1. mark it on UI
                item.setIcon(R.drawable.ic_action_favorites);
                // 2. mark it in memory
                GlobalData.aMovies[currentMoviePosition].setFavorite(true);
            }

            // 3. mark it on disk
            // GlobalData.database.update(Movie.MovieEntry.TABLE_NAME,GlobalData.aMovies[currentMoviePosition].getMovieEntry(), Movie.MovieEntry.COLUMN_ID + " = ?",new String[]{GlobalData.aMovies[currentMoviePosition].getsId()});

            ContentValues theRecord = GlobalData.aMovies[currentMoviePosition].getMovieEntry();
            String sMovieId = GlobalData.aMovies[currentMoviePosition].getsId();
            getContentResolver().update(Movie.MovieEntry.buildSetFavouriteMovieById(sMovieId),theRecord, Movie.MovieEntry.COLUMN_ID + " = ?",new String[]{sMovieId});

            return true;
        }

        if(id == R.id.action_share_trailer)
        {
            return true;
        }


        return super.onOptionsItemSelected(item);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);



            return rootView;
        }
    }

    private class ApiTrailerDataDownloader extends AsyncTask<String,Integer,String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            int i = Integer.parseInt(params[0]);

            String sMovieVideosUrl =  "http://api.themoviedb.org/3/movie/"+ GlobalData.aMovies[i].getsId() +"/videos?api_key=" + TopMoviesFragment.cApiKey;
            JSONRetriever jsonMovieVideosCall = new JSONRetriever(sMovieVideosUrl);

            aYouTubeKeys = new ArrayList<>();
            aYouTubeTitles = new ArrayList<>();
            String[] output;

            try {
                JSONArray jsonResults = jsonMovieVideosCall.message.getJSONArray("results");
                for (int j=0;j<jsonResults.length();j++){

                    JSONObject jsonTrailerData = jsonResults.getJSONObject(j);
                    if(jsonTrailerData.getString("site").equals("YouTube")){

                        aYouTubeKeys.add(jsonTrailerData.getString("key"));
                        aYouTubeTitles.add(jsonTrailerData.getString("name"));

                        ContentValues trailerRecord = new ContentValues();
                        trailerRecord.put(Movie.TrailerEntry.COLUMN_MOVIE_ID,GlobalData.aMovies[i].getsId());
                        trailerRecord.put(Movie.TrailerEntry.COLUMN_TRAILER_NAME,jsonTrailerData.getString("name"));
                        trailerRecord.put(Movie.TrailerEntry.COLUMN_YOUTUBE_KEY,jsonTrailerData.getString("key"));

                        // GlobalData.database.insert(Movie.TrailerEntry.TABLE_NAME,null,trailerRecord);
                        getContentResolver().insert(Movie.TrailerEntry.CONTENT_URI,trailerRecord);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            output = new String[aYouTubeTitles.size()];
            aYouTubeTitles.toArray(output);


            return output;
        }

        @Override
        protected void onPostExecute(String[] strings) {

            super.onPostExecute(strings);
            onTrailerDataPostExecute();



        }
    }

    private void onTrailerDataUpdated(){

        if(Build.VERSION.SDK_INT >= 14 && aYouTubeKeys.size() > 0) {

            ShareActionProvider trailerShareActionProvider;

            MenuItem menuShareItem = currentMenu.findItem(R.id.action_share_trailer);
            trailerShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuShareItem);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Trailer (" + GlobalData.aMovies[currentMoviePosition].getsOriginalTitle() +")");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Have fun! See here: " + "http://www.youtube.com/watch?v=" + aYouTubeKeys.get(0));
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=RFinNxS5KN4");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            trailerShareActionProvider.setShareIntent(shareIntent);

        }

    }

    private class ApiReviewDataDownloader extends AsyncTask<String,Integer,String[]> {

        @Override
        protected String[] doInBackground(String... params) {



            int i = Integer.parseInt(params[0]);

            String sMovieReviewsUrl =  "http://api.themoviedb.org/3/movie/"+ GlobalData.aMovies[i].getsId() +"/reviews?api_key=" + TopMoviesFragment.cApiKey;
            JSONRetriever jsonMovieReviewsCall = new JSONRetriever(sMovieReviewsUrl);

            aReviewAuthors = new ArrayList<>();
            aReviews = new ArrayList<>();
            String[] output;

            try {

                JSONArray jsonResults = jsonMovieReviewsCall.message.getJSONArray("results");
                for (int j=0;j<jsonResults.length();j++){

                    JSONObject jsonReviewData = jsonResults.getJSONObject(j);
                    aReviewAuthors.add(jsonReviewData.getString("author"));
                    aReviews.add(jsonReviewData.getString("content"));

                    ContentValues reviewRecord = new ContentValues();
                    reviewRecord.put(Movie.ReviewEntry.COLUMN_MOVIE_ID, GlobalData.aMovies[i].getsId());
                    reviewRecord.put(Movie.ReviewEntry.COLUMN_AUTHOR, jsonReviewData.getString("author"));
                    reviewRecord.put(Movie.ReviewEntry.COLUMN_REVIEW, jsonReviewData.getString("content"));

                    getContentResolver().insert(Movie.ReviewEntry.CONTENT_URI,reviewRecord);
                    // GlobalData.database.insert(Movie.ReviewEntry.TABLE_NAME,null,reviewRecord);

                }
            } catch (JSONException e) {

                e.printStackTrace();

            }

            output = new String[aReviewAuthors.size()];
            aReviewAuthors.toArray(output);

            return output;

        }

        @Override
        protected void onPostExecute(String[] strings) {

            super.onPostExecute(strings);
            onReviewDataPostExecute();

        }




    }

    protected void onTrailerDataPostExecute(){

        trailerListAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.trailer_list_item,R.id.trailer_item_text, aYouTubeTitles);
        trailerListView.setAdapter(trailerListAdapter);
        trailerListAdapter.notifyDataSetChanged();

        if(trailerListAdapter.getCount()>0) {

            final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            int iTotalListViewHeight = trailerListView.getDividerHeight();
            for(int i=0;i<trailerListAdapter.getCount();i++) {
                View childView = trailerListAdapter.getView(i, null, reviewListView);
                childView.measure(UNBOUNDED, UNBOUNDED);
                iTotalListViewHeight += childView.getMeasuredHeight() + trailerListView.getDividerHeight();

            }

            ViewGroup.LayoutParams layoutParams = trailerListView.getLayoutParams();
            layoutParams.height = iTotalListViewHeight;
            trailerListView.setLayoutParams(layoutParams);

        }


        onTrailerDataUpdated();

    }

    protected void onReviewDataPostExecute(){

        reviewListAdapter = new ReviewAdapter(getApplicationContext());
        reviewListView.setAdapter(reviewListAdapter);
        reviewListAdapter.notifyDataSetChanged();


        if(reviewListAdapter.getCount()>0) {

            if(findViewById(R.id.review_header_text_view) != null){
                findViewById(R.id.review_header_text_view).setVisibility(View.VISIBLE);
            }


            final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            int iTotalListViewHeight = reviewListView.getDividerHeight();
            for(int i=0;i<reviewListAdapter.getCount();i++) {
                View childView = reviewListAdapter.getView(i, null, reviewListView);
                childView.measure(UNBOUNDED, UNBOUNDED);
                iTotalListViewHeight += childView.getMeasuredHeight() + reviewListView.getDividerHeight();

            }

            ViewGroup.LayoutParams layoutParams = reviewListView.getLayoutParams();
            layoutParams.height = iTotalListViewHeight + (reviewListAdapter.getCount() + 1) * R.dimen.detail_fragment_overview_margin ;
            reviewListView.setLayoutParams(layoutParams);

        }


    }


    private class ReviewAdapter extends BaseAdapter {

        private Context theContext;

        ReviewAdapter(Context c){
            theContext = c;
        }

        @Override
        public int getCount() {
            if(aReviews != null) {
                return aReviews.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return aReviewAuthors.get(position) + "|" + aReviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;
            LayoutInflater inflater = (LayoutInflater) theContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                grid = inflater.inflate(R.layout.review_list_item,null);
                grid.setId(position);
            } else {
                grid = convertView;
            }

            TextView txtAuthorView = (TextView) grid.findViewById(R.id.review_author_text_view);
            txtAuthorView.setText(aReviewAuthors.get(position));

            TextView txtReviewView = (TextView) grid.findViewById(R.id.review_text_view);
            txtReviewView.setText(aReviews.get(position));

            return grid;
        }
    }


    private void getOfflineTrailerData(String sMovieId){

        int rowsCount;
        // Cursor dataSet = GlobalData.database.query(Movie.TrailerEntry.TABLE_NAME,null, Movie.TrailerEntry.COLUMN_MOVIE_ID + " = ?",new String[]{sMovieId},null,null,null);
        Cursor dataSet = getContentResolver().query(Movie.TrailerEntry.buildMovieTrailerById(sMovieId),null,null,null,null);
        rowsCount = dataSet.getCount();
        if(rowsCount > 0){
            aYouTubeTitles = new ArrayList<String>();
            aYouTubeKeys = new ArrayList<String>();
            for(int i=0;i<rowsCount;i++){
                dataSet.moveToPosition(i);
                aYouTubeTitles.add(dataSet.getString(dataSet.getColumnIndex(Movie.TrailerEntry.COLUMN_TRAILER_NAME)));
                aYouTubeKeys.add(dataSet.getString(dataSet.getColumnIndex(Movie.TrailerEntry.COLUMN_YOUTUBE_KEY)));
            }
            onTrailerDataPostExecute();
        }
        dataSet.close();

    }

    private void getOfflineReviewData(String sMovieId){

        int rowsCount;

        // Cursor dataSet = GlobalData.database.query(Movie.ReviewEntry.TABLE_NAME,null, Movie.ReviewEntry.COLUMN_MOVIE_ID + " = ?",new String[]{sMovieId},null,null,null);
        Cursor dataSet = getContentResolver().query(Movie.ReviewEntry.buildMovieReviewById(sMovieId),null,null,null,null);

        rowsCount = dataSet.getCount();
        if(rowsCount > 0){
            aReviewAuthors = new ArrayList<>();
            aReviews = new ArrayList<>();

            for(int i=0;i<rowsCount;i++){
                dataSet.moveToPosition(i);
                aReviewAuthors.add(dataSet.getString(dataSet.getColumnIndex(Movie.ReviewEntry.COLUMN_AUTHOR)));
                aReviews.add(dataSet.getString(dataSet.getColumnIndex(Movie.ReviewEntry.COLUMN_REVIEW)));
            }
            onReviewDataPostExecute();
        }
        dataSet.close();

    }

}
