package com.java.xtxnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


public class ShareFrameLayout extends FrameLayout {
    private int IMAGE_WIDTH;
    private int IMAGE_HEIGHT;
    private String qrCodeURL;
    private String imageURL;
    TextView summary,title;
    NetImageView netImageView;
    QRImageView qrImageView;
    public ShareFrameLayout(@NonNull Context context,String imageURL,String qrCodeURL) {
        super(context);
        this.qrCodeURL = qrCodeURL;
        this.imageURL = imageURL;
        if(imageURL.equals("")){
            IMAGE_WIDTH = 720;
            IMAGE_HEIGHT = 680;
        }else{
            IMAGE_WIDTH = 720;
            IMAGE_HEIGHT = 1280;
        }
        View view;
        if(imageURL.equals(""))
            view = View.inflate(getContext(), R.layout.share_image_layout1, this);
        else {
            view = View.inflate(getContext(), R.layout.share_image_layout2, this);
            netImageView = (NetImageView)view.findViewById(R.id.net_image);
            netImageView.setImageURL(imageURL);
        }
        qrImageView = (QRImageView)view.findViewById(R.id.qrcode);
        qrImageView.setURL(qrCodeURL);
        title = (TextView) view.findViewById(R.id.title);
        summary = (TextView)view.findViewById(R.id.summary);
    }

    void setSummary(String text){
        summary.setText(text);
    }
    void setTitle(String text){
        title.setText(text);
    }

    Bitmap createImage() {

        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_WIDTH, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_HEIGHT, MeasureSpec.EXACTLY);

        measure(widthMeasureSpec, heightMeasureSpec);
        layout(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        return bitmap;
    }
}
