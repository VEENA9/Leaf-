package com.veena.senz.leaf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

//import com.squareup.okhttp.Request;

//import okhttp3.MediaType;
//import okhttp3.MultipartBody;

//import com.android.volley.Request;
//import com.android.volley.Response;
//import okhttp3.OkHttpClient;


public class Photo extends AppCompatActivity {
    private Button mTakePhoto;
    private ImageView mImageView;
    private static final String TAG = "upload";
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private final int requestCode = 20;
    public static final int CAMERA_REQUEST_CODE = 228;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 4192;
    private File currentImage;
    private static final int CAMERA_PHOTO = 111;
    private static final int resultCode = -1;
    private Uri imageToUploadUri;
    public String url;
    private com.android.volley.Response.ErrorListener ErrorListener;
    private com.android.volley.Response.Listener Listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        url = "http://10.91.150.250:8080/api/auth/signin";
        mTakePhoto = (Button) findViewById(R.id.take_photo);
        mImageView = (ImageView) findViewById(R.id.imageView);


        mTakePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


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
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


                        if (requestCode == CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {
                            if (imageToUploadUri != null) {
                                Uri selectedImage = imageToUploadUri;
                                getContentResolver().notifyChange(selectedImage, null);
                                Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                                if (reducedSizeBitmap != null) {
                                    mImageView.setImageBitmap(reducedSizeBitmap);
                                    setImage();
                                } else {
                                    //makeText(this,"Error while capturing Image", LENGTH_LONG).show();
                                }
                            }
                        }


                    }


                }


            }


            private Bitmap getBitmap(String path) {

                Uri uri = Uri.fromFile(new File(path));
                InputStream in = null;
                try {
                    final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
                    in = getContentResolver().openInputStream(uri);

                    // Decode image size
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(in, null, o);
                    in.close();


                    int scale = 1;
                    while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                            IMAGE_MAX_SIZE) {
                        scale++;
                    }
                    Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

                    Bitmap b = null;
                    in = getContentResolver().openInputStream(uri);
                    if (scale > 1) {
                        scale--;
                        // scale to max possible inSampleSize that still yields an image
                        // larger than target
                        o = new BitmapFactory.Options();
                        o.inSampleSize = scale;
                        b = BitmapFactory.decodeStream(in, null, o);

                        // resize to desired dimensions
                        int height = b.getHeight();
                        int width = b.getWidth();
                        Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                        double y = Math.sqrt(IMAGE_MAX_SIZE
                                / (((double) width) / height));
                        double x = (y / height) * width;

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                                (int) y, true);
                        b.recycle();
                        b = scaledBitmap;

                        System.gc();
                    } else {
                        b = BitmapFactory.decodeStream(in);
                    }
                    in.close();

                    Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                            b.getHeight());
                    return b;
                } catch (IOException e) {
                    Log.e("", e.getMessage(), e);
                    return null;
                }
            }


//            protected void onActivityResult ( int requestCode, int resultCode, Intent data){
//            // TODO Auto-generated method stub
//            Log.i(TAG, "onActivityResult: " + this);
//            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
//                setPic();
//			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//			if (bitmap != null) {
//				mImageView.setImageBitmap(bitmap);
//				try {
//					sendPhoto(bitmap);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//            }
//        }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        makeText(this, "onACtivity", LENGTH_LONG).show();
        try {

            super.onActivityResult(requestCode, resultCode, data);
            if (this.CAMERA_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                makeText(this, data.getExtras().get("data").toString(), LENGTH_LONG).show();
//
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                FileOutputStream fo;
                try {
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new uploadFileToServerTask().execute(destination.getAbsolutePath());


                //
//                String partFilename = currentDateFormat();
//                storeCameraPhotoInSDCard(thumbnail, partFilename);
//
//                // display the image from SD Card to ImageView Control
//
//                String storeFilename =Environment.getExternalStorageDirectory()+ "/Leaf photo" + partFilename + ".jpg";
//                Bitmap mBitmap = getImageFileFromSDCard(storeFilename);
//                mImageView.setImageBitmap(mBitmap);
            }
        } catch (Exception ex) {
            makeText(Photo.this, ex.toString(),
                    LENGTH_SHORT).show();
        }


    }


    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "/Leaf photo" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //    public void onErrorResponse(VolleyError error) {
//        logYazdir("Error [" + error + "]");
//        Toast.makeText(getBaseContext(),
//                "Sunucuya bağlanılamadı!", Toast.LENGTH_LONG)
//                .show();
//
//    }
    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }




    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume: " + this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    String mCurrentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;

    private void dispatchTakePictureIntent() {
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/Leaf photo";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        currentImage = image;
        Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }

    private void setImage() {

        makeText(this, "set Image", LENGTH_LONG).show();
        try {
//            File f=new File(imgPath, "imgName.jpg");
            File f = currentImage;

            // Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView imageView = (ImageView) mImageView;
            imageView.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(new FileInputStream(f))));
            // mImageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    //
    public Photo sendImage() {
        RequestQueue mQueue = Volley.newRequestQueue(this);
        ConnectionImg imageUploadReq = new ConnectionImg(url, ErrorListener, Listener, currentImage);
        mQueue.add(imageUploadReq);
        return null;
    }


    public void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(Photo.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }


    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }}


//    private void send(){
//        String  url = "http://10.91.150.250:5000/api/auth/signin";
//
//// Example data
//        String username = "test_user_123";
//        String datetime = "2016-12-09 10:00:00";
//        File image = currentImage;
//// Create an HTTP client to execute the request
//        OkHttpClient client = new OkHttpClient();
//
//// Create a multipart request body. Add metadata and files as 'data parts'.
//        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("username", username)
//                .addFormDataPart("datetime", datetime)
//                .addFormDataPart("image", image.getName(),
//                        okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), image))
//                .build();
//
//// Create a POST request to send the data to UPLOAD_URL
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//// Execute the request and get the response from the server
//        Response response = null;
//
//        try {
//            response = client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//// Check the response to see if the upload succeeded
//        if (response == null || !response.isSuccessful()) {
//            Log.w("Example", "Unable to upload to server.");
//        } else {
//            Log.v("Example", "Upload was successful.");
//        }
//    }
//
//
//}


//        extends Activity {
//
//
//        private static final int CAMERA_REQUEST = 1888;
//        private ImageView imageView;
//        private static final int MY_CAMERA_PERMISSION_CODE = 100;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_photo);
//            this.imageView = (ImageView)this.findViewById(R.id.button4);
//            Button photoButton = (Button) this.findViewById(R.id.button3);
//            photoButton.setOnClickListener(new View.OnClickListener() {
//
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                @Override
//                public void onClick(View v) {
//                    if (checkSelfPermission(Manifest.permission.CAMERA)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                MY_CAMERA_PERMISSION_CODE);
//                    } else {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            if (requestCode == MY_CAMERA_PERMISSION_CODE) {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                } else {
//                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//                if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    imageView.setImageBitmap(photo);
//                }
//            }
//        }
//
//    public static void uploadimg(Bitmap.CompressFormat jpeg, int i, OutputStream output) {
//
//        ConnectionImg obj2=new ConnectionImg(CAMERA_REQUEST);
//    }
//
//}

