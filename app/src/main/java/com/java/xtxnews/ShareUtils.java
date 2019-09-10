package com.java.xtxnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class ShareUtils {
    static ShareFrameLayout shareFrameLayout=null;
    static void initShareImage(Context context,String title, String summary, String imageURL, String newsURL){
        shareFrameLayout = new ShareFrameLayout(context,imageURL,newsURL);
        shareFrameLayout.setTitle(title);
        shareFrameLayout.setSummary(summary);
    }
    static Uri createShareImage(Context context){
        Bitmap bitmap =shareFrameLayout.createImage();
        Uri path = saveImage(context,bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return path;
    }
    static private Uri saveImage(Context context, Bitmap bitmap) {

        File path = context.getCacheDir();

        String fileName = "shareImage.jpeg";

        File file = new File(path, fileName);

        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        return uri;
    }
}
