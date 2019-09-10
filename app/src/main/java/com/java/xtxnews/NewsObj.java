package com.java.xtxnews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsObj extends DataSupport {
    private String imageURLstr = null;
    private Date publishTime = null;
    private String keyWordstr = null;
    private String language = null;
    private String video = null;
    private String title = null;
    private String content = null;
    private String newsID = null;
    private String publisher = null;
    private String category = null;
    private String type = null;
    private String favorite = null;
    private String newsURL = null;
    public NewsObj(){
        super();
    }
    void parseJson(JSONObject jsonData){
        favorite = "false";
        List<String>imageURLList;
        try{
            String urls = jsonData.getString("image");
            urls = urls.substring(1,urls.length()-1);
            if(urls.length()==0) {
                imageURLList = new ArrayList<>();
            }else{
                imageURLList = Arrays.asList(urls.split(","));
            }
        }catch (Exception e){
            imageURLList = new ArrayList<>();
        }
        if(imageURLList.size()>=3)
            type = "MULPHOTONEWS";
        else if(imageURLList.size()>=1)
            type = "SIMPHOTONEWS";
        else
            type = "NOPHOTONEWS";

        StringBuilder stringBuilder = new StringBuilder("");
        for(String url:imageURLList){
            stringBuilder.append(url).append(",");
        }
        if(stringBuilder.length()>0)
            imageURLstr = stringBuilder.substring(0,stringBuilder.length()-1);
        else
            imageURLstr = "";

        String time = "";
        try{
            time = jsonData.getString("publishTime");
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            publishTime = ft.parse(time);
        }catch (Exception e){
            publishTime = null;
        }
        List<String> keyWords;
        try{
            keyWords = new ArrayList<>();
            JSONArray keywordsArr = jsonData.getJSONArray("keywords");
            for(int i=0;i<keywordsArr.length();i++){
                JSONObject jo = keywordsArr.getJSONObject(i);
                keyWords.add(jo.getString("word"));
            }
        }catch (Exception e){
            keyWords = new ArrayList<>();
        }

        StringBuilder sb = new StringBuilder("");
        for(String keyWord:keyWords)
            sb.append(keyWord).append(",");
        if(sb.length()>0)
            keyWordstr = sb.substring(0,sb.length()-1);
        else
            keyWordstr = "";
        try{
            language = jsonData.getString("language");
        }catch (Exception e){
            language = "zh-CN";
        }
        try{
            video = jsonData.getString("video");
        }catch (Exception e){
            video = "";
        }
        try{
            newsURL = jsonData.getString("url");
        }catch (Exception e){
            newsURL = "";
        }
        try{
            title = jsonData.getString("title");
        }catch (Exception e){
            title = "";
        }
        try{
            content = jsonData.getString("content");
        }catch (Exception e){
            content = "";
        }
        try{
            newsID = jsonData.getString("newsID");
        }catch (Exception e){
            newsID = "";
        }
        try{
            publisher = jsonData.getString("publisher");
        }catch (Exception e){
            publisher = "";
        }
        try{
            category = jsonData.getString("category");
        }catch (Exception e){
            category = "";
        }
    }
    boolean filter(List<String> blockWords){
        String[] keyWords = keyWordstr.split(",");
        for(String keyWord:keyWords)
            for(String blockWord:blockWords)
                if(keyWord.equals(blockWord))
                    return false;
        return true;
    }
    public String getImageURLstr(){
        return imageURLstr;
    }

    public void setImageURLstr(String imageURLstr) {
        this.imageURLstr = imageURLstr;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
    public String getFavorite(){
        return favorite;
    }
    public String getURL(int index){
        String[] urlList = imageURLstr.split(",");
        if(index>=urlList.length) {
            return "";
        }
        else {
            return urlList[index];
        }
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getNewsURL() {
        return newsURL;
    }

    public void setNewsURL(String newsURL) {
        this.newsURL = newsURL;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyWordstr() {
        return keyWordstr;
    }

    public void setKeyWordstr(String keyWordstr) {
        this.keyWordstr = keyWordstr;
    }
    public List<String> getKeyWords(){
        return Arrays.asList(keyWordstr.split(","));
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
