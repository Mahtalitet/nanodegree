package it.jaschke.alexandria.services;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.InputStream;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;

/**
 * Created by saj on 11/01/15.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap bookCover = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            bookCover = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bookCover;
    }



    protected void onPostExecute(Bitmap result) {

        bmImage.setImageBitmap(result);
        ViewGroup.LayoutParams lp = bmImage.getLayoutParams();
        Log.v("#1604a"," w = " + lp.width);

            if(result != null){

                int iWidth = result.getWidth();
                int iHeight = result.getHeight();

                float fRatio = (float)iHeight/(float)iWidth;
                Log.v("#1604b"," r = " + fRatio);

                Context c = MainActivity.appContext;
                WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
                Display d = wm.getDefaultDisplay();
                Point p = new Point();
                d.getSize(p);
                int screenWidth = p.x;

                //# Add tablet version !!!

                if(MainActivity.currentFragment != null && MainActivity.currentFragment.getTag() != null && MainActivity.currentFragment.getTag().equals(c.getResources().getString(R.string.book_details))){
                    lp.height = (int) (fRatio * screenWidth / 2);
                } else{
                    lp.height = (int) (fRatio * screenWidth / 5);
                }
                bmImage.setLayoutParams(lp);

            } else {

                lp.height = 0;
                bmImage.setLayoutParams(lp);

            }
    }
}

