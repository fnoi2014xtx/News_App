package com.java.xtxnews;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainViewPager extends ViewPager {
    private boolean slideable = false;
    private int startX,startY;
    private Context context;
    private AppCompatActivity activity;
    public void setSlideable(boolean slideable) {
        this.slideable = slideable;
    }
    public boolean getSlideable(){
        return slideable;
    }
    public MainViewPager(Context context){
        super(context);
        this.context = context;
    }
    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
    void setActivity(AppCompatActivity activity){
        this.activity = activity;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = (int)ev.getX();
                startY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dX= (int) (ev.getX()-startX);
                int dY= (int) (ev.getY()-startX);
                if(dX>0&&Math.abs(dX)>Math.abs(dY)) {
                    Intent intent = new Intent(activity, ChannelSideActivity.class);
                    activity.startActivityForResult(intent,MainActivity.CHANNEL_SIDE_RESULT_CODE);
                    //Toast.makeText(context,"hehehe",Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }

        return slideable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return slideable;
    }
}
