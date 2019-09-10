package com.java.xtxnews;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.content.Context;

import org.litepal.crud.DataSupport;

import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Response;

class StoreState{
    public String title;
    public String content;
    public String URL;
    public String shareImageURL;

    public StoreState( String title,String content, String shareImageURL, String URL){
        this.title = title;
        this.content = content;

        this.URL = URL;

        this.shareImageURL = shareImageURL;
    }
}

public class NewsIO {

    private static String currentUser;
    private static String passWord;
    private static String ip;
    private static String url;

    private static String sync;

    private static boolean logged = false;
    //private static boolean okGo = false;
    private static String compare;

    private static List<NewsObj> list = new ArrayList<NewsObj>();
    //private static List<NewsObj> list = Collections.synchronizedList(new ArrayList<NewsObj>());

    //private static List<String> blocked_list;//
    private static Map<String,String> plce;
    public static MainActivity currentActivity;

    //public static ArrayList<NewsObj> nowReading = new ArrayList<>();
    public static Map<String,StoreState> subscribed = new HashMap<>();


    public static void setMap(Map<String,String> mp){
        plce = mp; //
    }

    public static void setUser(String usr){currentUser = usr;}
    public static void setPassWord(String pswd){passWord = pswd;}
    public static void setIp(String ip_addr){ip=ip_addr;}


    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){// if not then search
            Search((String)msg.obj,msg.what);
        }
    };



    public static void Search( final String cmpare, final int page ){
        Log.w("url",url+"&page="+page);
        final String si = (url+"&page="+page);
        HttpsUtils.sendOkHttpRequest(si, new okhttp3.Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.w("fuck:",s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() == 0) {
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject js = jsonArray.getJSONObject(i);
                        if (js.getString("title").equals(cmpare)) {//
                            NewsObj newsObj = new NewsObj();
                            newsObj.parseJson(js);
                            List<NewsObj> list = DataSupport.where("newsID = ?",newsObj.getNewsID()).find(NewsObj.class);
                            if(list.size()==0) {
                                newsObj.setFavorite("true");
                                newsObj.save();
                            }else{
                                newsObj = list.get(0);
                                newsObj.setFavorite("true");
                                newsObj.updateAll("newsID = ?",newsObj.getNewsID());
                            }
                            Log.w("size:",list.size()+"");
                            Log.w("saved","saved");
                            return;
                        }

                    }

                    Message msg = new Message();
                    msg.what = page + 1;
                    msg.obj = cmpare;
                    handler.sendMessage(msg);
                    return;
                    // }
                } catch (Exception e) {

                }
            }

            public void onFailure(Call call, IOException e) {

            }
        });
    }


    public static void Generate(){// from parameters to news
        for(Map.Entry<String,String> entry:plce.entrySet()){
            url = "https://api2.newsminer.net/svc/news/queryNewsList?";
            String time = entry.getValue();
            url = url + "startDate="+time + "&endDate="+time;
            Search(entry.getKey(),1);
        }
        //return url.substring(0,url.length()-1);
    }

    public static void sync(){
        String lst = ""; String forth = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<NewsObj> newsObjList = DataSupport.where("favorite = ?","true").find(NewsObj.class);
        for(NewsObj obj : newsObjList){
            forth = forth + "\t"+obj.getTitle()+"\t"+formatter.format(obj.getPublishTime());

            //obj.setFavorite(String.valueOf(false));
            //obj.updateAll("newsID = ?",obj.getNewsID());
        }
        Log.w("forth",forth);
        Log.w("lst",lst);
        //Log.w("forth",forth);

        try{
            String temp1="";
            String temp2="";
            if(lst.length()>0){
                temp1 = URLEncoder.encode(lst,"utf-8");
            }
            if(forth.length()>0){
                temp2 = URLEncoder.encode(forth,"utf-8");
            }
            String path = "http://"+URLEncoder.encode(ip,"utf-8")+":8080/Web1/UserServlet?type="+URLEncoder.encode("modify","utf-8")+"&user="+URLEncoder.encode(currentUser,"utf-8")+"&password="+URLEncoder.encode(passWord,"utf-8")
                    +"&blocked="+temp1+"&subscribed="+temp2;
            HttpsUtils.sendOkHttpRequest(path,new okhttp3.Callback(){
                public void onResponse(Call call, Response response) throws IOException{
                    String output = response.body().string();
                    if(output.equals("Success")) {
                        sync = "Successfully synchronized";
                    }
                    else{
                        sync = "Sync Error: "+output;
                    }


                }
                public void onFailure(Call call, IOException e) {
                    sync = "Sync Error: Internet Failure";

                }

            });

        }catch(Exception e){
            e.printStackTrace();
            sync = "Sync Error: Connection failed";
        }

    }

    public static void delete(){
        //blocked_list.clear();
        List<NewsObj> newsObjList = DataSupport.where("favorite = ?","true").find(NewsObj.class);
        for(NewsObj obj : newsObjList){
            //forth = forth + "\t"+obj.getTitle()+"\t"+formatter.format(obj.getPublishTime());
            obj.setFavorite(String.valueOf(false));
            obj.updateAll("newsID = ?",obj.getNewsID());
        }
    }

    public static void getRecommend(String title,String category, List<String> words){

        list.clear();
        compare = title;
        final int out = words.size()<5?words.size():5;
        for( int i = 0; i < out; i ++) {//if nothing, then
            Log.w("get",words.get(i));
            String str = "https://api2.newsminer.net/svc/news/queryNewsList?"+"words="+words.get(i)+"&category="+category;
            HttpsUtils.sendOkHttpRequest(str,new okhttp3.Callback(){
                public void onResponse(Call call, Response response) throws IOException{// the
                    String output = response.body().string();
                    Log.w("This",output);
                    try {
                        JSONObject jsonObject = new JSONObject(output);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() == 0) {
                            //already = true;
                            return;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject js = jsonArray.getJSONObject(i);
                            if (!(js.getString("title").equals(compare))) {//
                                boolean equals = false;
                                Random rnd = new Random();
                                int t = rnd.nextInt(out);
                                for( int j = 0; j < list.size(); j ++){
                                    if((js.getString("title").equals(list.get(j).getTitle()))
                                        || (rnd.nextInt(out) == t)){//
                                        equals = true;
                                    }
                                }
                                if(equals == false) {
                                    NewsObj newsObj = new NewsObj();
                                    newsObj.parseJson(js);
                                    Log.w("finally", js.getString("title"));
                                    list.add(newsObj);
                                    return;
                                }
                            }

                        }
                    } catch (Exception e) {

                    }

                }
                public void onFailure(Call call, IOException e) {
                }

            });
        }
    }

    public static Map<String,String> getMap(){
        return plce;
    }

    public static List<NewsObj> getRecommendList(){ return list;}

    public static boolean getLogged(){return logged;}

    public static void setLogged( boolean log){ logged = log;}


}
