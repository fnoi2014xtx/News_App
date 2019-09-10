package com.java.xtxnews;

import android.app.Application;
import android.util.Log;

import com.zhy.changeskin.SkinManager;

public class NewsApplication extends org.litepal.LitePalApplication {
    private String themeName = "";
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
    }
    void setAppTheme(String themeName){
        Log.w("app_theme",themeName);
        this.themeName = themeName;
        SkinManager.getInstance().changeSkin(themeName);
    }
    String getAppTheme(){
        return themeName;
    }
}
