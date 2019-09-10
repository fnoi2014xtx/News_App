package com.java.xtxnews;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentFavorites extends Fragment {
    private int page;
    private List<NewsObj> newsList = Collections.synchronizedList(new ArrayList<NewsObj>());
    private NewsRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private TwinklingRefreshLayout twinklingRefreshLayout;
    public FragmentFavorites() {
        // Required empty public constructor
        page = 1;
    }
    private void flush(){
        newsList.clear();
        page=1;

        int offset = (page-1)*10;
        List<NewsObj> newsObjList = DataSupport.limit(10).offset(offset).where("favorite = ?","true").find(NewsObj.class);
        Log.w("size:",newsObjList.size()+"");
        newsList.addAll(newsObjList);
    }


    private void loadMore(){
        page ++;
        int offset = (page-1)*10;
        Log.w("offset:",offset+"");
        List<NewsObj> newsObjList = DataSupport.limit(10).offset(offset).where("favorite = ?","true").find(NewsObj.class);
        Log.w("loadmore:",newsObjList.size()+"");
        newsList.addAll(newsObjList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NewsRecyclerViewAdapter((MainActivity)getActivity(),newsList);
    }
    void refresh(){
        twinklingRefreshLayout.startRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        twinklingRefreshLayout =(TwinklingRefreshLayout)view.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager llm =new LinearLayoutManager((MainActivity)getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration((MainActivity)getActivity(),RecyclerView.VERTICAL));
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
                            Thread.sleep(500);
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
                            Thread.sleep(500);
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
