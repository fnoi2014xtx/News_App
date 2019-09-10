package com.java.xtxnews;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zhy.changeskin.SkinManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    static final int SEARCH_RESULT_CODE = 1;
    static final int CHANNEL_RESULT_CODE = 2;
    static final int BLOCK_RESULT_CODE = 3;
    static final int CHANNEL_SIDE_RESULT_CODE = 4;
    private FragmentNewsList fragmentNewsList;
    private FragmentFavorites fragmentFavorites;
    private FragmentLogin fragmentLogin;
    private FragmentLogout fragmentLogout;
    private int[] fragmentID = {0,1,2};
    MainViewPager viewPager;

    private List<String> channel_list = new ArrayList<>();
    private List<String> channel_lib = new ArrayList<>();
    private List<String> channel_del_list = new ArrayList<>();
    private List<String> block_list = new ArrayList<>();

    private Map<String,String> subscribedMap = new HashMap<String, String>();



    public MainActivity(){
        super();
        channel_lib.add("娱乐");
        channel_lib.add("军事");
        channel_lib.add("教育");
        channel_lib.add("文化");
        channel_lib.add("健康");
        channel_lib.add("财经");
        channel_lib.add("体育");
        channel_lib.add("汽车");
        channel_lib.add("科技");
        channel_lib.add("社会");
        channel_list.addAll(channel_lib);
    }

    void flipLogFragment(){
        if(fragmentID[2]==2)
            fragmentID[2]=3;
        else
            fragmentID[2]=2;
        viewPager.setCurrentItem(fragmentID[2]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        DataSupport.deleteAll(NewsObj.class);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //mTextMessage = findViewById(R.id);
        fragmentNewsList = new FragmentNewsList();
        fragmentFavorites = new FragmentFavorites();
        fragmentLogin = new FragmentLogin();
        fragmentLogout = new FragmentLogout();

        //NewsIO.setList(block_list);
        NewsIO.setMap(subscribedMap);

        viewPager = (MainViewPager)findViewById(R.id.main_vp);
        viewPager.setActivity(this);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentNewsList);
        fragmentList.add(fragmentFavorites);
        fragmentList.add(fragmentLogin);
        fragmentList.add(fragmentLogout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragmentPagerAdapter mainFragmentPagerAdapter = new MainFragmentPagerAdapter(fragmentManager,fragmentList);
        viewPager.setAdapter(mainFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        final MainActivity mainActivity = this;
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(fragmentID[0]);
                        //mTextMessage.setText(R.string.title_home);
                        break;
                    case R.id.navigation_favorites:
                        viewPager.setCurrentItem(fragmentID[1]);
                        //mTextMessage.setText(R.string.title_dashboard);
                        break;
                    case R.id.navigation_account:
                        viewPager.setCurrentItem(fragmentID[2]);
                        //mTextMessage.setText(R.string.title_notifications);
                        break;
                }
                mainActivity.invalidateOptionsMenu();
                NewsApplication newsApplication = (NewsApplication)getApplication();
                newsApplication.setAppTheme(newsApplication.getAppTheme());
                return true;
            }
        });

        NewsApplication newsApplication = (NewsApplication)getApplication();
        newsApplication.setAppTheme("");
        //replaceFragment(fragmentNewsList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    public List<String> getBlock_list() {
        return block_list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int id = viewPager.getCurrentItem();
        if(id==0){
            menu.findItem(R.id.menu_clear_data).setVisible(true);
            menu.findItem(R.id.menu_clear_favorites).setVisible(false);
            menu.findItem(R.id.menu_filter).setVisible(true);
            menu.findItem(R.id.menu_search_button).setVisible(true);
            menu.findItem(R.id.menu_theme_switch).setVisible(true);
        }else if(id==1){
            menu.findItem(R.id.menu_clear_data).setVisible(false);
            menu.findItem(R.id.menu_clear_favorites).setVisible(true);
            menu.findItem(R.id.menu_filter).setVisible(false);
            menu.findItem(R.id.menu_search_button).setVisible(false);
            menu.findItem(R.id.menu_theme_switch).setVisible(true);
        }else{
            menu.findItem(R.id.menu_clear_data).setVisible(false);
            menu.findItem(R.id.menu_clear_favorites).setVisible(false);
            menu.findItem(R.id.menu_filter).setVisible(false);
            menu.findItem(R.id.menu_search_button).setVisible(false);
            menu.findItem(R.id.menu_theme_switch).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    private void clearCachedData(){
        DataSupport.deleteAll(NewsObj.class);
    }
    private void clearFavorites(){
        NewsObj newsObj = new NewsObj();
        newsObj.setFavorite("false");
        newsObj.updateAll("favorite = ?","true");
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_clear_data:
                clearCachedData();
                fragmentFavorites.refresh();
                fragmentNewsList.refresh();
                break;
            case R.id.menu_clear_favorites:
                clearFavorites();
                fragmentFavorites.refresh();
                break;
            case R.id.menu_filter:
                intent = new Intent(MainActivity.this, ChannelActivity.class);
                intent.putExtra("selectedList",(Serializable) channel_list);
                intent.putExtra("deletedList",(Serializable) channel_del_list);
                startActivityForResult(intent,CHANNEL_RESULT_CODE);
                break;
            case R.id.menu_search_button:
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent,SEARCH_RESULT_CODE);
                break;
            case R.id.menu_theme_switch:
                NewsApplication newsApplication = (NewsApplication)getApplication();
                if(newsApplication.getAppTheme().equals(""))
                    newsApplication.setAppTheme("dark");
                else
                    newsApplication.setAppTheme("");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data==null){
            return;
        }
        switch (requestCode){
            case CHANNEL_RESULT_CODE:
                channel_list =  data.getStringArrayListExtra("selectedList");
                channel_del_list = data.getStringArrayListExtra("deletedList");
                fragmentNewsList.setCategories(channel_list);
                break;
            case BLOCK_RESULT_CODE:
                block_list = data.getStringArrayListExtra("blockList");
                fragmentNewsList.setBlockList(block_list);
                break;
            case SEARCH_RESULT_CODE:
                String keyWord = data.getStringExtra("searchKeyword");
                //Toast.makeText(this,"Get keyword = "+keyWord,Toast.LENGTH_SHORT).show();
                fragmentNewsList.setKeyWord(keyWord);
                break;
            case CHANNEL_SIDE_RESULT_CODE:
                String category = data.getStringExtra("category");
                channel_list.clear();
                channel_del_list.clear();
                for(String s:channel_lib)
                    if(s.equals(category))
                        channel_list.add(s);
                    else
                        channel_del_list.add(s);
                fragmentNewsList.setCategories(channel_list);
                fragmentNewsList.refresh();
                //Toast.makeText(this,category, Toast.LENGTH_SHORT).show();
                break;
        }
    }
    class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fragmentManager;
        private List<Fragment> fragmentList;
        public MainFragmentPagerAdapter(FragmentManager fragmentManager,List<Fragment> fragmentList){
            super(fragmentManager);
            this.fragmentManager = fragmentManager;
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
