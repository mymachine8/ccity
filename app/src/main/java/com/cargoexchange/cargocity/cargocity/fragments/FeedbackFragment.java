package com.cargoexchange.cargocity.cargocity.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cargoexchange.cargocity.cargocity.R;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedbackFragment extends Fragment
{
    //TODO:Implement the image window logic here
    private static final int SCAN_REQUEST_CODE = 99;
    private ImageView mCameraImage;
    private Activity mActivityContext;
    public FeedbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityContext = getActivity();
        try {
            mCameraImage = (ImageView) view.findViewById(R.id.camera_image);
            mCameraImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int preference = ScanConstants.OPEN_CAMERA;
                    Intent intent = new Intent(getActivity(), ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                    startActivityForResult(intent, SCAN_REQUEST_CODE);
                }
            });
        } catch(Exception ex) {
            Log.e("DELIVERY_FEEDBACK", ex.getMessage());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mActivityContext.getContentResolver(), uri);
                mActivityContext.getContentResolver().delete(uri, null, null);
                mCameraImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
