package com.jayangapp.healthier;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.loopj.android.http.AsyncHttpClient.log;

public class HomePage extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_PERMISSIONS_REQUEST =2;


    static  final String CLOUD_VISION_REQUEST_URL ="https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDnbrka7qti5lc6z_DZ3_Rb9wELod_Jvb0";


    Button txtR;
    Button photoR;
    ImageView mImageView;
    Bitmap bitmap;
    Uri photoUri;
    ArrayList<String> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        txtR = (Button)findViewById(R.id.textSearch);
        photoR=(Button)findViewById(R.id.photoSearch);
        mImageView =(ImageView)findViewById(R.id.imageView);
        bitmap =null;







    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Hello","Onstart called");
        txtR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this,MainActivity.class);
                startActivity(intent);



            }
        });

        photoR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder().create().show();




            }
        });

    }



    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        }


    // picture source from camera or gallery dialog
    public AlertDialog.Builder dialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        builder
                .setMessage(R.string.dialog_select_prompt)
                .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !setGalleryPermissionsRequest()) {
                            startGalleryChooser();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !setRequestImageCapture()) {
                            openCamera();
                        }


                    }

                });

        return builder;
    }

    public void startGalleryChooser() {
        if (!setGalleryPermissionsRequest()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_PERMISSIONS_REQUEST);
        }
    }

    //request user for Gallery permission
    private boolean setGalleryPermissionsRequest() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    GALLERY_PERMISSIONS_REQUEST);
            return true;
        }
        return false;
    }

    //request user for Camera Permission
    private boolean setRequestImageCapture(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openCamera();

                } else {

                    Toast.makeText(HomePage.this,"Camera Permission needed",Toast.LENGTH_SHORT).show();
                }
                break;


            }
            case GALLERY_PERMISSIONS_REQUEST:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startGalleryChooser();

                } else {

                    Toast.makeText(HomePage.this,"Permission to Access Gallery Needed",Toast.LENGTH_SHORT).show();
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File f = new File(mCurrentPhotoPath);
             photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", f);

            new msyncTask().execute(photoUri);





            Log.d("OnActivityResult","Camera");
        }
        if(requestCode == GALLERY_PERMISSIONS_REQUEST  &&data!=null){
            Log.d("OnActivityResult","Gallery");

            photoUri = data.getData();
            new msyncTask().execute(photoUri);
        }
    }








    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }



    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
// call Google Vision cloud vision api
    public void callGoogleVision(Bitmap bitmap) {
        JSONObject mJsonObject = new JSONObject();
        JSONObject imageJObj = new JSONObject();
        JSONObject featuresJObj = new JSONObject();
        JSONObject typeMaxRJObj = new JSONObject();
        JSONObject concatJObj = new JSONObject();
        JSONObject[] objs = new JSONObject[]{imageJObj, featuresJObj};
        String imageEncoded = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity stringEntity = null;


        try {
            imageJObj.put("image", new JSONObject().put("content", imageEncoded));
            typeMaxRJObj.put("type","LABEL_DETECTION");
            typeMaxRJObj.put("maxResults", 6);
            featuresJObj.put("features", new JSONArray().put(typeMaxRJObj));
            for (JSONObject obj : objs) {
                Iterator it = obj.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    concatJObj.put(key, obj.get(key));
                }

            }
            mJsonObject.put("requests", new JSONArray().put(concatJObj));

            Log.d("JsonMy", imageEncoded);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            stringEntity = new StringEntity(mJsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(this, CLOUD_VISION_REQUEST_URL, stringEntity, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("Respnse", response.toString());



                        try {
                           JSONArray jsonArray = response.getJSONArray("responses").getJSONObject(0).getJSONArray("labelAnnotations");
                            if(jsonArray!=null) {
                                arrayList = convertToArrList(jsonArray);
                                for (String str : arrayList) {
                                    Log.d("Label", str);
                                }
                                Intent i = new Intent(getBaseContext(), MainActivity.class);
                                i.putExtra("StringArrList", arrayList);
                                i.putExtra("BitmapUri", photoUri.toString());
                                startActivity(i);
                                finish();
                            }







                        } catch (JSONException e) {
                            Toast.makeText(HomePage.this,"Error Occured Try Different Picture",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Failed Response",responseString);
                    }
                });
    }

    private ArrayList<String> convertToArrList(JSONArray jsonArray){
        ArrayList<String> labelList = new ArrayList<>();
        for(int i =0; i<jsonArray.length();i++){
            try {
                labelList.add(jsonArray.getJSONObject(i).getString("description"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return labelList;
    }







    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
               bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);





            } catch (IOException e) {
                Log.d("upload", "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("upload", "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();

        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(),Base64.DEFAULT);
    }

    public class msyncTask extends AsyncTask<Uri,String,String>{



        @Override
        protected String doInBackground(Uri... params) {
            uploadImage(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            callGoogleVision(bitmap);



        }
    }












}




