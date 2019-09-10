package com.java.xtxnews;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentNewsList extends Fragment {
    private int page;
    private List<NewsObj> newsList = Collections.synchronizedList(new ArrayList<NewsObj>());
    private List<String> blockList = new ArrayList<>();
    private Map<String,String> parameters = new HashMap<String, String>();
    private NewsRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    TwinklingRefreshLayout twinklingRefreshLayout;
    public FragmentNewsList() {
        // Required empty public constructor
        parameters.put("size","10");
        parameters.put("words","");
        parameters.put("page","1");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        parameters.put("endDate",df.format(new Date()));
        parameters.put("categories","娱乐,军事,教育,文化,健康,财经,体育,汽车,科技,社会");
        page = 1;
    }

    void setCategories(List<String> categories){
        StringBuilder c = new StringBuilder();
        for(String category:categories){
            c.append(category);
            c.append(",");
        }
        if(c.length()==0)
            c.append("不存在,");
        String c_str = c.substring(0,c.length()-1);
        parameters.put("categories",c_str);
        refresh();
    }
    void setBlockList(List<String> blockList){
        this.blockList = blockList;
        refresh();
    }
    void setKeyWord(String keyWord){
        parameters.put("words",keyWord);
        refresh();
    }
    private void updateTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        parameters.put("endDate",df.format(new Date()));
    }
    private void setPage(int page){
        this.page = page;
        parameters.put("page",String.valueOf(page));
    }
    private String generateURL(){
        StringBuilder url = new StringBuilder("https://api2.newsminer.net/svc/news/queryNewsList?");
        for(Map.Entry<String,String> entry:parameters.entrySet()){
            String parameter = entry.getKey();
            String value = entry.getValue();
            url.append(parameter).append("=").append(value).append("&");
        }
        return url.substring(0,url.length()-1);
    }
    private void flush(){
        newsList.clear();
        setPage(1);

        if(!HttpsUtils.isNetWorkConnected(getActivity())){
            int offset = (page-1)*10;
            List<NewsObj> newsObjList = DataSupport.limit(100).find(NewsObj.class);
            for(NewsObj newsObj:newsObjList){
                if(newsObj.filter(blockList)&&parameters.get("categories").contains(newsObj.getCategory())&&newsObj.getContent().contains(parameters.get("words"))){
                    newsList.add(newsObj);
                }
            }
            return;
        }


        updateTime();

        String url = generateURL();
        HttpsUtils.sendOkHttpRequest(url,new okhttp3.Callback(){
            public void onResponse(Call call, Response response)throws IOException {
                String s = response.body().string();
                if(s.length()==0)
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    jsonArray.getJSONObject(1);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        NewsObj newsObj = new NewsObj();
                        newsObj.parseJson(jsonArray.getJSONObject(i));

                        if(newsObj.filter(blockList)) {
                            newsList.add(newsObj);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            public void onFailure(Call call, IOException e){
            }
        } );
    }


    private void loadMore(){
        setPage(page+1);

        if(!HttpsUtils.isNetWorkConnected(getActivity())){
            int offset = (page-1)*100;
            List<NewsObj> newsObjList = DataSupport.limit(100).offset(offset).find(NewsObj.class);
            for(NewsObj newsObj:newsObjList){
                if(newsObj.filter(blockList)&&parameters.get("categories").contains(newsObj.getCategory())&&newsObj.getContent().contains(parameters.get("words"))){
                    newsList.add(newsObj);
                }
            }
            return;
        }

        String url = generateURL();
        HttpsUtils.sendOkHttpRequest(url,new okhttp3.Callback(){
            public void onResponse(Call call, Response response)throws IOException {
                String s = response.body().string();
                if(s.length()==0)
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        NewsObj newsObj = new NewsObj();
                        newsObj.parseJson(jsonArray.getJSONObject(i));
                        if(newsObj.filter(blockList))
                            newsList.add(newsObj);
                    }
                }catch (Exception e){
                }
            }
            public void onFailure(Call call, IOException e){
            }
        } );
    }

    void refresh(){
        twinklingRefreshLayout.startRefresh();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        twinklingRefreshLayout =(TwinklingRefreshLayout)view.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager llm =new LinearLayoutManager((MainActivity)getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration((MainActivity)getActivity(),RecyclerView.VERTICAL));
        adapter = new NewsRecyclerViewAdapter(getActivity(),newsList);
        recyclerView.setAdapter(adapter);
        twinklingRefreshLayout.startRefresh();
        twinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flush();
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e){}
                        adapter.notifyDataSetChanged();
                        refreshLayout.finishRefreshing();
                    }
                },1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e){}
                        adapter.notifyDataSetChanged();
                        refreshLayout.finishLoadmore();
                    }

                },1000);
            }
        });

        Connector.getDatabase();
        return view;
    }

}
