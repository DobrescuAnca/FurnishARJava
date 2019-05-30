package com.example.furnishar.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class PhotoUtils {

    public static String convertToBase64(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] pictureData = baos.toByteArray();

        return Base64.encodeToString(pictureData, Base64.DEFAULT);
    }
}
