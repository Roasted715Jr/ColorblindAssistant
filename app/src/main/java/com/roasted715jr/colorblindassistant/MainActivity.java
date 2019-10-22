package com.roasted715jr.colorblindassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

//https://developer.android.com/training/camera/photobasics
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1234;

    Bitmap imageBitmap, rotatedBitmap;
    Button btnTakePicture;
    ImageView imgThumbnail;
    String currentPhotoPath;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }

        btnTakePicture = (Button) findViewById(R.id.btn_take_picture);
        imgThumbnail = (ImageView) findViewById(R.id.img_thumbnail);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        //Get the pixel colors
        imgThumbnail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Image view is 1440px by 1920px
                //Image size is 4032px by 3024px
                int x = (int) (event.getX() / 1440 * 4032); //This is too big to access the bitmap
                int y = (int) (event.getY() / 1920 * 3024);
                Log.d(TAG, "Position: " + x + ", " + y);

                //Width of the square area we want to average
                int squareSide = 21;

                //[x][y][color]
                int[][][] pixels = new int[squareSide][squareSide][3];

                //Get each pixel's rgb
                for (int i = 0; i < pixels.length; i++) {
                    for (int j = 0; j < pixels[i].length; j++) {
//                        pixels[i][j][0] = Color.red(imageBitmap.getPixel(x + i - (int) (squareSide / 2.0), y + j - (int) (squareSide / 2.0)));
//                        pixels[i][j][1] = Color.green(imageBitmap.getPixel(x + i - (int) (squareSide / 2.0), y + j - (int) (squareSide / 2.0)));
//                        pixels[i][j][2] = Color.blue(imageBitmap.getPixel(x + i - (int) (squareSide / 2.0), y + j - (int) (squareSide / 2.0)));
                        pixels[i][j][0] = Color.red(rotatedBitmap.getPixel(x + i, y + j));
                        pixels[i][j][1] = Color.green(rotatedBitmap.getPixel(x + i, y + j));
                        pixels[i][j][2] = Color.blue(rotatedBitmap.getPixel(x + i, y + j));
                    }
                }

                int[] pixelsAvg = new int[3];

                //Average the values of each pixel
                for (int i = 0; i < pixels.length; i++) {
                    for (int j = 0; j < pixels[i].length; j++) {
                        pixelsAvg[0] += pixels[i][j][0];
                        pixelsAvg[1] += pixels[i][j][1];
                        pixelsAvg[2] += pixels[i][j][2];
                    }
                }

                pixelsAvg[0] /= Math.pow(squareSide, 2);
                pixelsAvg[1] /= Math.pow(squareSide, 2);
                pixelsAvg[2] /= Math.pow(squareSide, 2);

//                String output = "";
//                for (int i = 0; i < pixels.length; i++) {
//                    for (int j = 0; j < pixels[i].length; j++) {
//
//                    }
//                }

                Log.d(TAG, "Raw: " + Arrays.deepToString(pixels));
                Log.d(TAG, "Color: " + pixelsAvg[0] + ", " + pixelsAvg[1] + ", " + pixelsAvg[2]);

                return false; //Not sure what true would do
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotatedBitmap != null)
            imgThumbnail.setImageBitmap(rotatedBitmap);
    }

    //Retrieve the returned thumbnail image and display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//            imgThumbnail.setImageBitmap(imageBitmap);

            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmpFactoryOptions);
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(90);
            rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), rotateMatrix, false);
            imgThumbnail.setImageBitmap(rotatedBitmap);
        }

        galleryAddPic();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted");
                } else {
                    Log.d(TAG, "Permission Denied");
                    Toast.makeText(this, "Camera permission required for app to work", Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

    //Get a camera app to take a picture, and request the image back
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Only proceed if the file was successfully created
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.roasted715jr.colorblindassistant", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //Including this makes the thumbnail not work
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + "Colorblind Assistant"); //This directory makes these photos private to the app by default
//        File storageDir = getExternalFilesDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Colorblind Assistant");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        //Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "Image created and stored successfully");
        return image;
    }

    //Add the photo to the media gallery to make available system-wide
    private void galleryAddPic() {

//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);

        grantUriPermission("com.roasted715jr.colorblindassistant", Uri.fromFile(f), 0);
        MediaStore.Images.Media.insertImage(getContentResolver(), rotatedBitmap, "Title" , "Description");

        Log.d(TAG, "This jawn should be in the gallery now");
    }
}
