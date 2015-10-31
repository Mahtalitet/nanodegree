package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.*;
import com.larvalabs.svgandroid.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.ParseException;


/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends CursorAdapter
{
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    // --- Version 6.0 --->
    public static final int COL_HOME_CREST_LINK = 10;
    public static final int COL_AWAY_CREST_LINK = 11;
    public static final int COL_HOME_TEAM_ID = 12;
    public static final int COL_AWAY_TEAM_ID = 13;

    private static int TEMP_COUNTER = 2;
    private static int BITMAP_IMAGE_QUALITY = 50;
    private static int CREST_MAX_SIZE = 1024;
    private static Bitmap.Config BITMAP_COLOR_CODING = Bitmap.Config.ARGB_8888;
    private static Bitmap.CompressFormat BITMAP_COMPRES_FORMAT = Bitmap.CompressFormat.PNG;


    // <--- Version 6.0 ---


    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = MainActivity.theContext.getString(R.string.app_name);
    public scoresAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);

        // --- Version 6.0 --->

        final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mItem.measure(UNBOUNDED, UNBOUNDED);
        int iWidth = mItem.getMeasuredWidth();
        ViewGroup.LayoutParams lp = mItem.findViewById(R.id.home_crest).getLayoutParams();
        lp.height = iWidth;
        mItem.findViewById(R.id.home_crest).setLayoutParams(lp);
        mItem.findViewById(R.id.away_crest).setLayoutParams(lp);

        // <--- Version 6.0 ---


        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));

        // --- Version 6.0 --->
        String sMatchTime = cursor.getString(COL_MATCHTIME);
        if(sMatchTime.contains(context.getString(R.string.match_status_finished_db))){
            mHolder.date.setText(context.getString(R.string.match_status_finished));

        } else {
            mHolder.date.setText(sMatchTime);
        }
        // mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        // <--- Version 6.0 ---

        mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS),cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)
        ));


        // --- Version 6.0 --->

        if(TEMP_COUNTER > 0) {

            ImageLoader imageLoaderForHomeCrest = new ImageLoader(mHolder.home_crest, context.getResources().getDrawable(R.drawable.no_crest_192), cursor.getString(COL_HOME_CREST_LINK), cursor.getInt(COL_HOME_TEAM_ID));
            imageLoaderForHomeCrest.loadImageToImageView();

            ImageLoader imageLoaderForAwayCrest = new ImageLoader(mHolder.away_crest, context.getResources().getDrawable(R.drawable.no_crest_192), cursor.getString(COL_AWAY_CREST_LINK), cursor.getInt(COL_AWAY_TEAM_ID));
            imageLoaderForAwayCrest.loadImageToImageView();

            //TODO Send task to the queue

            // TEMP_COUNTER--;
        }
        // <--- Version 6.0 ---


        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id)
        {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText()+" "
                    +mHolder.score.getText()+" "+mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType(MainActivity.mainPagerFragment.getString(R.string.mime_type_text));
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }


    // --- Version 6.0 --->

    private class ImageLoader extends AsyncTask<String,Integer, Boolean> {

        Drawable pPlaceholderDrawable;
        ImageView pImageView;
        StringBuffer pImageLink;
        int pTeamId;

        String xFileName;
        Drawable xPictureDrawable;
        SVG xSVG;

        ImageLoader (ImageView imageView, Drawable placeholderDrawable, String imageLink, int teamId ){

            super();

            pImageLink = new StringBuffer();
            pPlaceholderDrawable = placeholderDrawable;
            pImageView = imageView;
            pImageLink.append(imageLink);
            pTeamId = teamId;

            Uri xUri = Uri.parse(pImageLink.toString());

            try {

                String sNewSubUri = URLEncoder.encode(xUri.getPath(),"UTF-8");

                if(!sNewSubUri.equals(xUri.getPath())){

                    String sOriginalUri = xUri.toString();
                    String sNewUri = sOriginalUri.replace(xUri.getPath(),"") + sNewSubUri;
                    String sNewerUri = sNewUri.replace("%2F","/");
                    pImageLink = new StringBuffer();
                    pImageLink.append(sNewerUri);
                    xUri = Uri.parse(pImageLink.toString());

                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            if(xUri.getScheme() == null){
                pImageLink.insert(0,"/");
                xUri = Uri.parse(pImageLink.toString());
                pImageLink = null;
                pImageLink = new StringBuffer();
                pImageLink.append(xUri.buildUpon().scheme("http").build().toString());
            }

            if(xUri.getHost() != null){
                pImageLink = null;
                pImageLink = new StringBuffer();
                pImageLink.append(xUri.buildUpon().scheme("https").build().toString());
            } else {
                Log.v("#1432","No host? " + xUri.getHost() + " for " + pImageLink );
            }



            if(BITMAP_COMPRES_FORMAT.equals(Bitmap.CompressFormat.PNG)){
                xFileName = pTeamId + MainActivity.theContext.getResources().getString(R.string.file_extension_png);
            } else {
                xFileName = pTeamId + MainActivity.theContext.getResources().getString(R.string.file_extension_jpg);
            }



        }


        @Override
        protected Boolean doInBackground(String... params) {

            URL imageUrl;
            HttpURLConnection imageConnection;

            try {


                imageUrl = new URL(pImageLink.toString());


                imageConnection = (HttpURLConnection) imageUrl.openConnection();
                imageConnection.setRequestMethod(MainActivity.theContext.getString(R.string.request_format_get));

                imageConnection.connect();

                if (imageConnection == null) {
                    Log.v("#SVG", "Cannot establish connection with " + pImageLink);
                    return false;
                }

                SVGBuilder svgBuilder = new SVGBuilder();
                int iDataLength = imageConnection.getContentLength();
                Log.v("#SVG", "Remote file size = " + iDataLength + " at " + pImageLink);


                if (svgBuilder != null) {
                    Log.v("#SVG", "SVGBuilder initialized for " + pImageLink);
                    logMemoryValue(null);


                    //#Error if less than 2 colors

                    xSVG = svgBuilder.readFromInputStream(imageConnection.getInputStream()).setCloseInputStreamWhenDone(true).build();
                    Log.v("#SVG", "SVGBuilder read from " + pImageLink);
                    //  = svgBuilder.build();
                    if (xSVG != null) {
                        Log.v("#SVG", "SVG created from " + pImageLink + " size " + xSVG.getLimits().width() + "x" + xSVG.getLimits().height());


                    } else {
                        Log.v("#SVG", "SVG not created for " + pImageLink);
                    }


                    svgBuilder = null;

                } else {
                    Log.v("#SVG", "SVG parsing failure for " + pImageLink + "...");
                }


                imageConnection.disconnect();

            } catch (java.lang.NumberFormatException e) {

                Log.e("SVG Parser", "Invalid SVG file content");
                e.printStackTrace();

            } catch (java.lang.IllegalArgumentException e){

                Log.e("SVG Parser", "Invalid SVG file content");
                e.printStackTrace();

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (SVGParseException e) {

                Log.v("#SVG", "SVG parsing exception for " + pImageLink);

            }

            catch (IOException e) {

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();
                Log.v("#1252", e.getMessage() + pImageLink);


            }


            finally {

            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(xSVG != null) {

                FileOutputStream fOut = null;

                    try {

                        fOut = MainActivity.theContext.openFileOutput(xFileName, 0);
                        Bitmap xBitmap = null;

                        int iOriginalWidth = (int)xSVG.getLimits().width();
                        int iOriginalHeight = (int)xSVG.getLimits().height();

                        int iRescaledWidth = 0;
                        int iRescaledHeight = 0;

                        if(iOriginalWidth > CREST_MAX_SIZE || iOriginalHeight > CREST_MAX_SIZE){

                            iRescaledHeight = 192;
                            iRescaledWidth = (int)(iOriginalWidth*iRescaledHeight/iOriginalHeight);

                        } else {

                            iRescaledHeight = iOriginalHeight;
                            iRescaledWidth = iOriginalWidth;

                        }


                        xBitmap = Bitmap.createBitmap(iRescaledWidth,iRescaledHeight, BITMAP_COLOR_CODING);
                        Canvas canvas = new Canvas(xBitmap);
                        canvas.drawPicture(xSVG.getPicture());

                        if(xBitmap.compress(BITMAP_COMPRES_FORMAT, BITMAP_IMAGE_QUALITY, fOut)){
                            Log.v("#SVG","SVG image stored to file " + xFileName +", size:" +iRescaledWidth + "x" + iRescaledHeight);
                        } else {
                            Log.v("#SVG","SVG image not stored to file " + xFileName);
                        }


                        xBitmap.recycle();
                        // canvas = null;
                        xSVG = null;
                        xPictureDrawable = null;

                        fOut.close();

                        String sLocalPath = MainActivity.theContext.getFilesDir() + "/" + xFileName;
                        pImageView.setImageBitmap(BitmapFactory.decodeFile(sLocalPath));

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

//                }
            }


            super.onPostExecute(aBoolean);

        }

        private void loadImageToImageView()  {


            String sLocalPath = MainActivity.theContext.getFilesDir() + "/" + xFileName;
            File f = new File(sLocalPath);


            //
            // File must be non-zero length!
            //

            if(f.exists() && f.length() > 0)
            {

                pImageView.setImageBitmap(BitmapFactory.decodeFile(sLocalPath));


            } else {

                // --- Get file extension ---

                Uri imgUri = Uri.parse(pImageLink.toString());

                String sOriginalFileName = imgUri.getLastPathSegment();

                if(sOriginalFileName != null) {


                    String sFileExtension = sOriginalFileName.substring(sOriginalFileName.lastIndexOf('.'));

                    sFileExtension = sFileExtension.toLowerCase();

                    final Context ctx = MainActivity.theContext;

                    if (sFileExtension.equals(ctx.getString(R.string.file_extension_svg))) {

                        execute();

                    } else {

                        Drawable d = ctx.getResources().getDrawable(R.drawable.no_crest_192);
                        callPicassoWithUrl(ctx, pImageLink.toString(), d, xFileName, pImageView);

                    }

                }

            }

        }

        private void callPicassoWithUrl(final Context ctx, String sUrl, Drawable placeholderDrawable, final String sFileName, final ImageView imageView){

            Picasso.with(ctx).load(sUrl).placeholder(placeholderDrawable).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {

                        FileOutputStream fOut = ctx.openFileOutput(sFileName, 0);
                        bitmap.compress(BITMAP_COMPRES_FORMAT, BITMAP_IMAGE_QUALITY, fOut);
                        fOut.close();
                        imageView.setImageBitmap(bitmap);

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
                    imageView.setImageDrawable(placeHolderDrawable);
                }
            });

        }


    }


    private void logMemoryValue (@Nullable String sTag){
        if(sTag == null){
            sTag = "#file_loader";
        }
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        final int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);

        Log.v(sTag, "Max VM = " + maxMemory + ", free VM = " + freeMemory + ", total VM = " + totalMemory);
    }



}
