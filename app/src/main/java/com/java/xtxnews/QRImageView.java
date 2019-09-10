package com.java.xtxnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class QRImageView extends AppCompatImageView {
    public QRImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QRImageView(Context context) {
        super(context);
    }

    public QRImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setURL(final String url) {
        Bitmap bitmap = QRUtils.createQRImage(url,150,150);
        //Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.icon);
        setImageBitmap(bitmap);
    }
}
