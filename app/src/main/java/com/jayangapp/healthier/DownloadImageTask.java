package com.jayangapp.healthier;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by yjj781265 on 8/3/2017.
 * Make bitmap from given Url in the background thread
 */

 class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String[] urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("NO image found", e.getMessage());


        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(result==null){
            bmImage.setImageResource(R.drawable.no_image_found);
        }else {
            bmImage.setImageBitmap(result);
        }

    }
}