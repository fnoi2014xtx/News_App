package com.java.xtxnews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.zhy.changeskin.SkinManager;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_search);
        SearchView searchView = (SearchView) findViewById(R.id.search_view);

        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAction(String string) {
                Intent intent =new Intent();
                intent.putExtra("searchKeyword",string);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        searchView.setOnClickBack(new BCallBack() {
            @Override
            public void BackAction() {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SkinManager.getInstance().unregister(this);
    }
}
