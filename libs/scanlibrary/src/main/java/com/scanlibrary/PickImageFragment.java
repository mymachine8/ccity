package com.scanlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mymachine on 18/01/16.
 */
public class PickImageFragment extends Fragment {

    private static final int REQUEST_CODE_ASK_PERMISSION_CAMERA = 50;
    private View view;
    private ImageButton cameraButton;
    private ImageButton galleryButton;
    private Uri fileUri;
    private IScanner scanner;
    private ScanActivity thisActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pick_image_fragment, null);
        thisActivity=(ScanActivity)getActivity();
        init();
        return view;
    }

    private void init() {
        cameraButton = (ImageButton) view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new CameraButtonClickListener());
        galleryButton = (ImageButton) view.findViewById(R.id.selectButton);
        galleryButton.setOnClickListener(new GalleryClickListener());
        if (isIntentPreferenceSet()) {
            handleIntentPreference();
        }
    }

    private void handleIntentPreference() {
        int preference = getIntentPreference();
        if (preference == ScanConstants.OPEN_CAMERA) {
            requestCameraPermission();
        } else if (preference == ScanConstants.OPEN_MEDIA) {
            openMediaContent();
        }
    }

    private boolean isIntentPreferenceSet() {
        int preference = getArguments().getInt(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
        return preference != 0;
    }

    private int getIntentPreference() {
        int preference = getArguments().getInt(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
        return preference;
    }


    private class CameraButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            requestCameraPermission();
        }
    }

    private class GalleryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            openMediaContent();
        }
    }

    public void openMediaContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int hasreadWriteStoragePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED && hasreadWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSION_CAMERA);
            return;
        }
    }

    public void openCamera() {
        //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent cameraIntent = new Intent(thisActivity,CameraCustom.class);
        File file = createImageFile();
        file.getParentFile().mkdirs();
        fileUri = Uri.fromFile(file);
        if (file != null) {
            //TODO:Create a call to the custom camera interface
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE);
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp + ".jpg");
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null,temp=null;
        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {
                    case ScanConstants.START_CAMERA_REQUEST_CODE:
                        //bitmap = getBitmap(fileUri);
                        temp=BitmapFactory.decodeFile(data.getData().toString());
                        bitmap=Bitmap.createScaledBitmap(temp,800,640,false);
                        break;

                    case ScanConstants.PICKFILE_REQUEST_CODE:
                        bitmap = getBitmap(data.getData());
                        break;
                }
            } catch (Exception e
                    ) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            postImagePick(bitmap);
        }
    }

    protected void postImagePick(Bitmap bitmap) {
        Uri uri = Utils.getUri(getActivity(), bitmap);
        bitmap.recycle();
        scanner.onBitmapSelect(uri);
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor =
                getActivity().getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }
}