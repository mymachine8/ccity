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
    File rootFile = new File(Environment.getExternalStorageDirectory(), "Sign");
    private String fileName;
    public boolean saveScreen(View view)
    {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }
        if (!rootFile.exists()) {
            rootFile.mkdir();
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        try {
            File file = new File(rootFile,System.currentTimeMillis() + ".jpg");
            fileName  = file.getAbsolutePath();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            view.setDrawingCacheEnabled(false);
            bitmap = null;
        }
    }

    public String getSaveSignFileName() {
        return fileName;
    }
}
