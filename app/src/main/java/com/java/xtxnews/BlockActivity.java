package com.java.xtxnews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhy.changeskin.SkinManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockActivity extends Activity implements AdapterView.OnItemClickListener {

    private KeyWordGridView blockGV,alterGV;
    private List<String> blockList=new ArrayList<>(),alterList = new ArrayList<>();
    private KeyWordAdapter blockAdapter,alterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockList = getIntent().getStringArrayListExtra("blockList");
        alterList = getIntent().getStringArrayListExtra("alterList");
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_block_selector);
        initView();
        View trans_view = (View)findViewById(R.id.transparent_view);
        trans_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button button = (Button) findViewById(R.id.block_selector_confirm);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.putExtra("blockList",(Serializable) blockList);
                intent.putExtra("alterList",(Serializable) alterList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    public void initView() {
        blockGV = (KeyWordGridView) findViewById(R.id.blockGridView);
        alterGV = (KeyWordGridView) findViewById(R.id.alterGridView);
        blockAdapter = new KeyWordAdapter(this, blockList,true);
        alterAdapter = new KeyWordAdapter(this, alterList,false);
        blockGV.setAdapter(blockAdapter);
        alterGV.setAdapter(alterAdapter);

        blockGV.setOnItemClickListener(this);
        alterGV.setOnItemClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }
    private ViewGroup getMoveViewGroup() {
        //window中最顶层的view
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final String moveChannel,
                          final GridView clickGridView, final boolean isUser) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(100L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // 判断点击的是DragGrid还是OtherGridView
                if (isUser) {
                    alterAdapter.setVisible(true);
                    alterAdapter.notifyDataSetChanged();
                    blockAdapter.remove();
                } else {
                    blockAdapter.setVisible(true);
                    blockAdapter.notifyDataSetChanged();
                    alterAdapter.remove();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final ImageView moveImageView = getView(view);
        switch (parent.getId()) {
            case R.id.blockGridView:

                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.tv_des);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final String channel = ((KeyWordAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                    alterAdapter.setVisible(false);
                    //添加到最后一个
                    alterAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                alterGV.getChildAt(alterGV.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, blockGV, true);
                                blockAdapter.setRemove(position);
                                /*if(NewsIO.getLogged()) {
                                    //NewsIO.setList();
                                    NewsIO.sync();
                                }*/

                            } catch (Exception localException) { }
                        }
                    }, 50L);
                }
                break;
            case R.id.alterGridView:
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.tv_des);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final String channel = ((KeyWordAdapter) parent.getAdapter()).getItem(position);
                    blockAdapter.setVisible(false);
                    //添加到最后一个
                    blockAdapter.addItem(channel);
                    /*if(NewsIO.getLogged()) {
                        //NewsIO.setList(blockList);
                        NewsIO.sync();
                    }*/
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                blockGV.getChildAt(blockGV.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, alterGV,false);
                                alterAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }
}
