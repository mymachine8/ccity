package com.cargoexchange.cargocity.cargocity.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by thespidy on 28/01/16.
 */
public class ImageHelper {
    public static String convertBitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] byteImage= baos.toByteArray();
        return Base64.encodeToString(byteImage, Base64.DEFAULT);
    }
}
