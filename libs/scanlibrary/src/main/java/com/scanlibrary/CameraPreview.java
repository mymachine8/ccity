package com.scanlibrary;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by root on 20/1/16.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static Context mContext;
    public CameraPreview(Context context,Camera camera)
    {
        super(context);
        mContext=context;
        mCamera=camera;
        setCameraDisplayOrientation(mCamera);
        mHolder=getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("CameraExceptions", "Error setting surface to camera: function name(CameraPreview.surfaceCreated)");
        }

    }
    public static void setCameraDisplayOrientation(Camera camera)
    {
        int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
        Camera.CameraInfo info=new Camera.CameraInfo();
        //Camera.getCameraInfo(cameraId,info);

        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 90; break;
            case Surface.ROTATION_90: degrees = 0; break;
            case Surface.ROTATION_180: degrees = 270; break;
            case Surface.ROTATION_270: degrees = 180; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else
        {  // back-facing
            result = (info.orientation + degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //Handle change in preview size and orientation here
        //Preview should be stopped before making any changes in size and orientation
        mCamera.stopPreview();
        setCameraDisplayOrientation(mCamera);
        mCamera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d("DesCameraException","Surface Destroyed");
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }
    /*@Override
    protected void onDraw(Canvas canvas)
    {

        canvas.drawRect(10,10,10,10,new Paint(Color.RED));
        Log.w(this.getClass().getName(), "On Draw Called");
    }*/
}
