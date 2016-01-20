package com.scanlibrary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by root on 20/1/16.
 */
public class CameraInitializations
{
    public boolean checkCameraHardware(Context context)
    {
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            Log.d("CameraExceptions", "Has Camera=true");
            return true;
        }
        else
        {
            return false;
        }
    }

    public Camera getCameraInstance()
    {
        Camera c=null;
        try
        {
            c=Camera.open();  //by default it access the back camera
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.d("Camera Exceptions","Camera Not found");
        }
        return c;   //returns null if camera is not available
    }

}

