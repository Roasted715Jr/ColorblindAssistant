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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//https://developer.android.com/training/camera/photobasics
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1234;

    Bitmap imageBitmap, rotatedBitmap;
    Button btnTakePicture;
    ImageView imgThumbnail, colorBox;
    String currentPhotoPath;
    TextView txtColor;

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
        colorBox = (ImageView) findViewById(R.id.img_color);
        txtColor = (TextView) findViewById(R.id.txt_color);

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
                if (imageBitmap != null) { //So we don't crash when the image doesn't exist and we tap the ImageView
                    //Image view is 1440px by 1920px
                    //Image size is 4032px by 3024px
                    int x = (int) (event.getX() * 1.8);
                    int y = (int) (event.getY() * 1.8);
                    Log.d(TAG, "Position: " + x + ", " + y);

                    //Width of the square area we want to average
                    int squareSide = 3;

                    //[x][y][color]
                    int[][][] pixels = new int[squareSide][squareSide][3];

                    //Get each pixel's rgb
                    for (int i = 0; i < pixels.length; i++) {
                        for (int j = 0; j < pixels[i].length; j++) {
                            pixels[i][j][0] = Color.red(imageBitmap.getPixel(x + i - squareSide / 2, y + j - squareSide / 2));
                            pixels[i][j][1] = Color.green(imageBitmap.getPixel(x + i - 1, y + j - 1));
                            pixels[i][j][2] = Color.blue(imageBitmap.getPixel(x + i - 1, y + j - 1));
                        }
                    }

                    //Average the values of each pixel
                    int r = 0, g = 0, b = 0;

                    for (int i = 0; i < pixels.length; i++) {
                        for (int j = 0; j < pixels[i].length; j++) {
                            r += pixels[i][j][0];
                            g += pixels[i][j][1];
                            b += pixels[i][j][2];
                        }
                    }

                    r /= Math.pow(squareSide, 2);
                    g /= Math.pow(squareSide, 2);
                    b /= Math.pow(squareSide, 2);

                    //                 Log.d(TAG, "Raw: " + Arrays.deepToString(pixels));
                    Log.d(TAG, "Color: " + r + ", " + g + ", " + b);

                    Bitmap colorBitmap = Bitmap.createBitmap(colorBox.getWidth(), colorBox.getHeight(), Bitmap.Config.RGBA_F16);
                    colorBitmap.eraseColor(Color.rgb(r, g, b)); //Fills bitmap with specified color
                    colorBox.setImageBitmap(colorBitmap);

//                    txtColor.setText(Colors.getColorSimple(r, g, b) + "\nRed: " + r + "\nGreen: " + g + "\nBlue: " + b);

                    float[] hsv = new float[3];
                    Color.colorToHSV(Color.rgb(r, g, b), hsv);
                    Color color = Color.valueOf(Color.rgb(r, g, b));
//                    txtColor.setText(Colors.getColorSimple(r, g, b) + "\nH: " + hsv[0] + "\nS: " + hsv[1] + "\nV: " + hsv[2]);
                    txtColor.setText(Colors.getColor(color) + "\nH: " + hsv[0] + "\nS: " + hsv[1] + "\nV: " + hsv[2]);
                }

                return false;
            }
        });

//        imgThumbnail.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int x = (int) (event.getX() * 1.75);
//                int y = (int) (event.getY() * 1.75);
//                Log.d(TAG, "" + x + ", " + y);
//
//                int pixel = imageBitmap.getPixel(x, y);
////                Log.d(TAG, String.valueOf(pixel));
//
//                int r = Color.red(pixel);
//                int g = Color.green(pixel);
//                int b = Color.blue(pixel);
//                Log.d(TAG, r + ", " + g + ", " + b);
//
////                colorBox.setBackgroundColor(Color.rgb(r, g, b));
////                colorBox.setImageBitmap(Bitmap.createBitmap(new int[] {r, g, b, 1}, colorBox.getWidth(), colorBox.getHeight(), Bitmap.Config.RGBA_F16));
//
//                Bitmap color = Bitmap.createBitmap(colorBox.getWidth(), colorBox.getHeight(), Bitmap.Config.RGBA_F16);
//                color.eraseColor(Color.rgb(r, g, b));
//                colorBox.setImageBitmap(color);
//
//                return false; //Not sure what true would do
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageBitmap != null)
            imgThumbnail.setImageBitmap(imageBitmap);
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
            imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), rotateMatrix, false);
            imgThumbnail.setImageBitmap(imageBitmap);
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
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/Colorblind Assistant/"); //This directory makes these photos private to the app by default
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
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
        MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "Title" , "Description");

        Log.d(TAG, "This jawn should be in the gallery now");
    }
}
