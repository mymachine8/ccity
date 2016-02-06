package com.scanlibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraCustom extends AppCompatActivity
{
    private CameraInitializations mCameraIntializations;
    private android.hardware.Camera mCamera;
    private CameraPreview mCameraPreview;
    private ImageButton mCaptureButton;
    private static String FILE_NAME=new String();
    private FrameLayout preview;
    ImageButton no;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_custom);

        mCameraIntializations=new CameraInitializations();
        boolean mCameraAvailabilty=mCameraIntializations.checkCameraHardware(CameraCustom.this);
        if(mCameraAvailabilty)
        {
            mCamera=mCameraIntializations.getCameraInstance();
            mCameraPreview=new CameraPreview(CameraCustom.this,mCamera);
            preview=(FrameLayout)findViewById(R.id.camera_preview);
            preview.addView(mCameraPreview);

            //View importCapture = ((ViewStub) findViewById(R.id.capture_stub)).inflate();
            mCaptureButton=(ImageButton)findViewById(R.id.button_capture);
            mCaptureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.takePicture(null, null, mPicture);
                }
            });
        }
    }

    private android.hardware.Camera.PictureCallback mPicture=new android.hardware.Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera)
        {
            //Create a File to store image
            final byte[] mdata = data;
            //View importPanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();
            mCaptureButton.setVisibility(View.INVISIBLE);
            final ImageButton yes = (ImageButton)findViewById(R.id.capturecorrectbutton);
            yes.setVisibility(View.VISIBLE);
            no = (ImageButton)findViewById(R.id.capturewrongbutton);
            no.setVisibility(View.VISIBLE);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File pictureFile = getOutputMediaFile();
                    if (pictureFile == null)
                    {
                        Log.d("CameraException", "Unable to create file");
                        return;
                    }
                    try
                    {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(mdata);
                        fos.close();
                        Log.d("Debuggingcameracamracla", Uri.parse(FILE_NAME).toString());
                        Log.d("Debuggingcameracamracla",FILE_NAME);
                        Intent FeedbackIntent=new Intent();
                        FeedbackIntent.setData(Uri.parse(FILE_NAME));
                        setResult(RESULT_OK, FeedbackIntent);
                        finish();
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            no.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    mCameraPreview.setVisibility(View.VISIBLE);
                    mCaptureButton.setVisibility(View.VISIBLE);
                    yes.setVisibility(View.INVISIBLE);
                    no.setVisibility(View.INVISIBLE);
                    mCamera.startPreview();
                    Log.d("CameraExc", "no onclick");
                }
            });
        }
    };
    private static File getOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CargoExchange");
        if (! mediaStorageDir.exists())
        {
            if (! mediaStorageDir.mkdirs())
            {
                Log.d("CameraException", "Failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        FILE_NAME=mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
        File mediaFile;
        mediaFile = new File(FILE_NAME);
        return mediaFile;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
