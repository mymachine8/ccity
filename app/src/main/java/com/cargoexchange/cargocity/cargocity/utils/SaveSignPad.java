package com.cargoexchange.cargocity.cargocity.utils;

/**
 * Created by root on 1/2/16.
 */
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by root on 1/2/16.
 */
public class SaveSignPad
{
    private static final File rootDir = new File(Environment.getExternalStorageDirectory()+File.separator+"sign/");

    public static boolean saveScreen(View view)
    {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(rootDir, System.currentTimeMillis() + ".jpg")));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            view.setDrawingCacheEnabled(false);
            bitmap = null;
        }
    }
}
