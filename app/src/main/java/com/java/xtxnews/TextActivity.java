package com.java.xtxnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.NestedScrollingChild;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.changeskin.SkinManager;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.util.Random;


public class TextActivity extends AppCompatActivity {

    private boolean favorite = false;
    private String newsID = null;
    private String title;
    private String content;
    private String shareImageURL="";
    private String URL = "";
    private String category;// partant
    private ArrayList<String> words;

    private ArrayList<NewsObj> recommendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_text);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        favorite = getIntent().getBooleanExtra("favorite",false);
        newsID = getIntent().getStringExtra("newsID");
        URL = getIntent().getStringExtra("URL");

        category = getIntent().getStringExtra("category");
        words = getIntent().getStringArrayListExtra("keywords");

        String URLstr = getIntent().getStringExtra("imgURLs");
        String[] urls;
        if(URLstr.equals(""))
            urls = new String[0];
        else
            urls = URLstr.split(",");
        if(urls.length>0)
            shareImageURL = urls[0];

        ShareUtils.initShareImage(TextActivity.this,title,content,shareImageURL,URL);

        NewsIO.getRecommend(title,category,words);
        Thread newThread = new Thread(){
            @Override
            public void run(){
                while(NewsIO.getRecommendList().size()==0) {
                    try {
                        Thread.sleep(2000);
                    }
                    catch(Exception e){}
                }
                recommendList.addAll(NewsIO.getRecommendList());
            }
        };

        newThread.start();



        TextView title_tv = (TextView) findViewById(R.id.news_title);
        TextView content_tv = (TextView) findViewById(R.id.news_content);
        title_tv.setText(title);
        content_tv.setText(content);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager llm =new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        TextRecyclerViewAdapter adapter = new TextRecyclerViewAdapter(this, Arrays.asList(urls));
        recyclerView.setAdapter(adapter);
        recyclerView.setVerticalScrollBarEnabled(false);
    }

    /*public void refreshIntent(StoreState i){

       /* title = i.title;
        content = i.content;
        favorite = i.favorite;
        newsID = i.newsID;
        URL = i.URL;

        category = i.category;
        words = i.words;

        shareImageURL = i.shareImageURL;*/



        /*NewsIO.getRecommend(title,category,words);
        Thread newThread = new Thread(){
            @Override
            public void run(){
                while(NewsIO.getRecommendList().size()==0) {
                    try {
                        Thread.sleep(2000);
                    }
                    catch(Exception e){}
                }
                recommendList.addAll(NewsIO.getRecommendList());
            }
        };

        newThread.start();*/



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_activity_menu,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        NewsObj newsObj = new NewsObj();
        newsObj.setFavorite(String.valueOf(favorite));
        newsObj.updateAll("newsID = ?",newsID);
        if(NewsIO.getLogged()) {
            NewsIO.sync();
        }
        super.onBackPressed();
        if(NewsIO.subscribed.containsKey(URL)) {
            StoreState s = NewsIO.subscribed.get(URL);
            ShareUtils.initShareImage(TextActivity.this,s.title,s.content,s.shareImageURL,s.URL);
            NewsIO.subscribed.remove(URL);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_share:
                Uri imgURL = ShareUtils.createShareImage(TextActivity.this);//how to share
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM,imgURL);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent,"分享到..."));
                break;
            case R.id.menu_theme_switch:
                NewsApplication newsApplication = (NewsApplication) getApplication();
                if(newsApplication.getAppTheme().equals(""))
                    newsApplication.setAppTheme("dark");
                else
                    newsApplication.setAppTheme("");
                break;

            case R.id.menu_favor:
                if(NewsIO.getLogged()==true) {
                    if (favorite) {
                        Toast.makeText(TextActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                        favorite = !favorite;
                    } else {
                        Toast.makeText(TextActivity.this, "添加收藏", Toast.LENGTH_SHORT).show();
                        favorite = !favorite;
                    }
                    // NewsIO.subscribed.put(URL,favorite);
                }else{
                    Toast.makeText(TextActivity.this, "错误：未登录", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menu_recommend:
               // List<NewsObj> recommendList = NewsIO.getRecommendList()
               // Toast.makeText(TextActivity.this,"请稍等...",Toast.LENGTH_SHORT).show();
                while( recommendList.size() == 0){
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Toast.makeText(TextActivity.this,"加载错误",Toast.LENGTH_SHORT).show();
                    }
                }
                Random r = new Random();
                int number = r.nextInt(recommendList.size());
                NewsObj newsObj = recommendList.get(number);


                List<NewsObj> list = DataSupport.where("newsID = ?",newsObj.getNewsID()).find(NewsObj.class);//has or not?

                Intent newIntent = new Intent(this,TextActivity.class);
                newIntent.putExtra("title",newsObj.getTitle());
                newIntent.putExtra("content",newsObj.getContent());
                newIntent.putExtra("newsID",newsObj.getNewsID());
                newIntent.putExtra("imgURLs",newsObj.getImageURLstr());
                newIntent.putExtra("URL",newsObj.getNewsURL());

                ArrayList<String> lst = new ArrayList<String>();
                lst.addAll(newsObj.getKeyWords());

                newIntent.putExtra("category",newsObj.getCategory());
                newIntent.putStringArrayListExtra("keywords",lst);

                if (list.size() == 0){
                    newsObj.setFavorite("false");
                    newsObj.save();
                    newIntent.putExtra("favorite",false);
                    // NewsIO.subscribed.put(newsObj.getNewsURL(),false);
                }else{
                    newsObj = list.get(0);
                    if(newsObj.getFavorite().equals("true")) {
                        newIntent.putExtra("favorite", true);
                    //    NewsIO.subscribed.put(newsObj.getNewsURL(),true);
                    }
                    else {
                        newIntent.putExtra("favorite", false);
                    //    NewsIO.subscribed.put(newsObj.getNewsURL(),false);
                    }
                }
                NewsIO.subscribed.put(newsObj.getNewsURL(),new StoreState(title,content,shareImageURL,URL));
                //NewsIO.nowReading.add(newsObj);
                this.startActivity(newIntent);
                break;
        }
        return true;
    }
}
