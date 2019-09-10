package com.java.xtxnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChannelSideActivity extends Activity {

    List<String> categories = new ArrayList<>();
    public ChannelSideActivity(){
        categories.add("娱乐");
        categories.add("军事");
        categories.add("教育");
        categories.add("文化");
        categories.add("健康");
        categories.add("财经");
        categories.add("体育");
        categories.add("汽车");
        categories.add("科技");
        categories.add("社会");
    }

    void finishWithCategory(String category){
        Intent intent = new Intent();
        intent.putExtra("category",category);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_side);
        SkinManager.getInstance().register(this);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.category_view);
        LinearLayoutManager llm =new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        final SideRecyclerViewAdapter adapter = new SideRecyclerViewAdapter(this, categories);
        recyclerView.setAdapter(adapter);
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.setOnClick(new SideRecyclerViewAdapter.ClickInterface() {
                    @Override
                    public void onItemClick(Context context,String category) {
                        //Toast.makeText(context,category,Toast.LENGTH_SHORT).show();
                        finishWithCategory(category);
                    }
                });
            }
        });
        View view =(View)findViewById(R.id.transparent_view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
