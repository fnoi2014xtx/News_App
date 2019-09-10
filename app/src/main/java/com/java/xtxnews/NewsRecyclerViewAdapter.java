package com.java.xtxnews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NO_PHOTO_NEWS = 0;
    private static final int SIM_PHOTO_NEWS = 1;
    private static final int MUL_PHOTO_NEWS = 2;
    private Context context;
    private List<NewsObj> newsList;
    NewsRecyclerViewAdapter(Context context,List<NewsObj> newsList){
        this.newsList = newsList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        String type = newsList.get(position).getType();
        if(type.equals("NOPHOTONEWS"))
            return NO_PHOTO_NEWS;
        else if(type.equals("SIMPHOTONEWS"))
            return SIM_PHOTO_NEWS;
        else if(type.equals("MULPHOTONEWS"))
            return MUL_PHOTO_NEWS;
        return -1;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case NO_PHOTO_NEWS:
                return new NoPhotoNewsViewHolder(inflater.inflate(R.layout.news_item_layout1,parent,false));
            case SIM_PHOTO_NEWS:
                return new SimPhotoNewsViewHolder(inflater.inflate(R.layout.news_item_layout2, parent, false));
            case MUL_PHOTO_NEWS:
                return new MulPhotoNewsViewHolder(inflater.inflate(R.layout.news_item_layout3,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainActivity mainActivity = (MainActivity)context;

        if(holder instanceof NoPhotoNewsViewHolder){
            final NoPhotoNewsViewHolder hd = (NoPhotoNewsViewHolder) holder;
            hd.newsObj = newsList.get(position);
            final List<NewsObj> list = DataSupport.where("newsID = ?",hd.newsObj.getNewsID()).find(NewsObj.class);
            Log.w("onBindViewHolder",hd.newsObj.getTitle() + ": "+ list.size());
            if(list.size()>0) {
                hd.title.setText("[已缓存]" + hd.newsObj.getTitle());
                hd.title.setTextColor(context.getColor(R.color.NewsListTitleCached));
                Log.w("onBindViewHolderSet",String.valueOf(hd.title.getText()));
            }
            else {
                hd.title.setText(hd.newsObj.getTitle());
                hd.title.setTextColor(context.getColor(R.color.NewsListTitle));
            }
            hd.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,TextActivity.class);
                    intent.putExtra("title",hd.newsObj.getTitle());
                    intent.putExtra("content",hd.newsObj.getContent());
                    intent.putExtra("newsID",hd.newsObj.getNewsID());
                    intent.putExtra("imgURLs",hd.newsObj.getImageURLstr());
                    intent.putExtra("URL",hd.newsObj.getNewsURL());

                    ArrayList<String> lst = new ArrayList<String>();
                    lst.addAll(hd.newsObj.getKeyWords());

                    intent.putExtra("category",hd.newsObj.getCategory());
                    intent.putStringArrayListExtra("keywords",lst);

                    if (list.size() == 0){
                        hd.newsObj.setFavorite("false");
                        hd.newsObj.save();
                        hd.title.setText("[已缓存]"+hd.newsObj.getTitle());
                        hd.title.setTextColor(context.getColor(R.color.NewsListTitleCached));
                        intent.putExtra("favorite",false);
                    }else{
                        NewsObj newsObj = list.get(0);
                        if(newsObj.getFavorite().equals("true"))
                            intent.putExtra("favorite",true);
                        else
                            intent.putExtra("favorite",false);
                    }
                    context.startActivity(intent);

                }
            });
            hd.publishTime.setText(DateUtils.getRelativeTimeSpanString(hd.newsObj.getPublishTime().getTime()));
            hd.publisher.setText(hd.newsObj.getPublisher());
            hd.summary.setText(hd.newsObj.getContent());
            final List<String> alterList = hd.newsObj.getKeyWords();//change list
            hd.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = (MainActivity)context;
                    Intent intent = new Intent(activity,BlockActivity.class);
                    intent.putExtra("blockList",(Serializable) ((MainActivity) activity).getBlock_list());
                    intent.putExtra("alterList",(Serializable) alterList);
                    activity.startActivityForResult(intent,((MainActivity) activity).BLOCK_RESULT_CODE);
                    //Toast.makeText(((Activity)context),"Delete Me !",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(holder instanceof SimPhotoNewsViewHolder){
            final SimPhotoNewsViewHolder hd = (SimPhotoNewsViewHolder) holder;
            hd.newsObj = newsList.get(position);
            final List<NewsObj> list = DataSupport.where("newsID = ?",hd.newsObj.getNewsID()).find(NewsObj.class);
            Log.w("onBindViewHolder",hd.newsObj.getTitle() + ": "+ list.size());
            if(list.size()>0){
                hd.title.setText("[已缓存]"+hd.newsObj.getTitle());
                hd.title.setTextColor(context.getColor(R.color.NewsListTitleCached));
                Log.w("onBindViewHolderSet",String.valueOf(hd.title.getText()));
            }else{
                hd.title.setText(hd.newsObj.getTitle());
                hd.title.setTextColor(context.getColor(R.color.NewsListTitle));
            }
            hd.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,TextActivity.class);
                    intent.putExtra("title",hd.newsObj.getTitle());
                    intent.putExtra("content",hd.newsObj.getContent());
                    intent.putExtra("newsID",hd.newsObj.getNewsID());
                    intent.putExtra("imgURLs",hd.newsObj.getImageURLstr());
                    intent.putExtra("URL",hd.newsObj.getNewsURL());

                    ArrayList<String> lst = new ArrayList<String>();
                    lst.addAll(hd.newsObj.getKeyWords());

                    intent.putExtra("category",hd.newsObj.getCategory());
                    intent.putStringArrayListExtra("keywords",lst);

                    if (list.size() == 0){
                        hd.newsObj.setFavorite("false");
                        hd.newsObj.save();
                        hd.title.setText("[已缓存]"+hd.newsObj.getTitle());
                        hd.title.setTextColor(context.getColor(R.color.NewsListTitleCached));
                        intent.putExtra("favorite",false);
                    }else{
                        NewsObj newsObj = list.get(0);
                        if(newsObj.getFavorite().equals("true"))
                            intent.putExtra("favorite",true);
                        else
                            intent.putExtra("favorite",false);
                    }
                    context.startActivity(intent);
                }
            });
            hd.publishTime.setText(DateUtils.getRelativeTimeSpanString(hd.newsObj.getPublishTime().getTime()));
            hd.publisher.setText(hd.newsObj.getPublisher());
            hd.image.setImageURL(hd.newsObj.getURL(0));
            final List<String> alterList = hd.newsObj.getKeyWords();
            hd.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = (MainActivity)context;
                    Intent intent = new Intent(activity,BlockActivity.class);
                    intent.putExtra("blockList",(Serializable) ((MainActivity) activity).getBlock_list());
                    intent.putExtra("alterList",(Serializable) alterList);
                    activity.startActivityForResult(intent,((MainActivity) activity).BLOCK_RESULT_CODE);
                    //Toast.makeText(((Activity)context),"Delete Me !",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(holder instanceof MulPhotoNewsViewHolder){
            final MulPhotoNewsViewHolder hd = (MulPhotoNewsViewHolder) holder;
            hd.newsObj = newsList.get(position);
            final List<NewsObj> list = DataSupport.where("newsID = ?",hd.newsObj.getNewsID()).find(NewsObj.class);
            Log.w("onBindViewHolder",hd.newsObj.getTitle() + ": "+ list.size());
            if(list.size()>0){
                hd.title.setText("[已缓存]"+hd.newsObj.getTitle());
                hd.title.setTextColor(context.getColor(R.color.NewsListTitleCached));
                Log.w("onBindViewHolderSet",String.valueOf(hd.title.getText()));
            } else{
                hd.title.setText(hd.newsObj.getTitle());
                hd.title.setTextColor(context.getColor(R.color.NewsListTitle));
            }
            hd.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,TextActivity.class);
                    intent.putExtra("title",hd.newsObj.getTitle());
                    intent.putExtra("content",hd.newsObj.getContent());
                    intent.putExtra("newsID",hd.newsObj.getNewsID());
                    intent.putExtra("imgURLs",hd.newsObj.getImageURLstr());
                    intent.putExtra("URL",hd.newsObj.getNewsURL());

                    ArrayList<String> lst = new ArrayList<String>();
                    lst.addAll(hd.newsObj.getKeyWords());

                    intent.putExtra("category",hd.newsObj.getCategory());
                    intent.putStringArrayListExtra("keywords",lst);

                    if (list.size() == 0){
                        hd.newsObj.setFavorite("false");
                        hd.newsObj.save();
                        hd.title.setText("[已缓存]"+hd.newsObj.getTitle());
                        hd.title.setTextColor(context.getColor(R.color.NewsListTitleCached));
                        intent.putExtra("favorite",false);
                    }else{
                        NewsObj newsObj = list.get(0);
                        if(newsObj.getFavorite().equals("true"))
                            intent.putExtra("favorite",true);
                        else
                            intent.putExtra("favorite",false);
                    }
                    context.startActivity(intent);
                }
            });
            hd.publishTime.setText(DateUtils.getRelativeTimeSpanString(hd.newsObj.getPublishTime().getTime()));
            hd.publisher.setText(hd.newsObj.getPublisher());
            hd.image1.setImageURL(hd.newsObj.getURL(0));
            hd.image2.setImageURL(hd.newsObj.getURL(1));
            hd.image3.setImageURL(hd.newsObj.getURL(2));
            final List<String> alterList = hd.newsObj.getKeyWords();
            hd.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = (MainActivity)context;
                    Intent intent = new Intent(activity,BlockActivity.class);
                    intent.putExtra("blockList",(Serializable) ((MainActivity) activity).getBlock_list());
                    intent.putExtra("alterList",(Serializable) alterList);
                    activity.startActivityForResult(intent,((MainActivity) activity).BLOCK_RESULT_CODE);
                    //Toast.makeText(((Activity)context),"Delete Me !",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    //具体item数据等于pages*10，每页10条
    @Override
    public int getItemCount() {
        return newsList.size();
    }
    class NoPhotoNewsViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView publishTime;
        TextView publisher;
        TextView summary;
        NewsObj newsObj;
        ImageView deleteButton;
        NoPhotoNewsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            publishTime = (TextView) itemView.findViewById(R.id.publishTime);
            publisher = (TextView) itemView.findViewById(R.id.publisher);
            summary = (TextView) itemView.findViewById(R.id.summary);
            deleteButton =(ImageView)itemView.findViewById(R.id.deleteButton);
        }
    }

    class SimPhotoNewsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView publishTime;
        TextView publisher;
        ImageView deleteButton;
        NetImageView image;
        NewsObj newsObj;
        SimPhotoNewsViewHolder(View itemView) {
            super(itemView);
            image = (NetImageView) itemView.findViewById(R.id.simple_photo);
            title = (TextView) itemView.findViewById(R.id.title);
            publishTime = (TextView) itemView.findViewById(R.id.publishTime);
            publisher = (TextView) itemView.findViewById(R.id.publisher);
            deleteButton = (ImageView) itemView.findViewById(R.id.deleteButton);
        }
    }

    class MulPhotoNewsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView publishTime;
        TextView publisher;
        ImageView deleteButton;
        NetImageView image1,image2,image3;
        NewsObj newsObj;
        MulPhotoNewsViewHolder(View itemView) {
            super(itemView);
            image1 = (NetImageView)itemView.findViewById(R.id.mul_photos_01);
            image2 = (NetImageView)itemView.findViewById(R.id.mul_photos_02);
            image3 = (NetImageView)itemView.findViewById(R.id.mul_photos_03);

            title = (TextView) itemView.findViewById(R.id.title);
            publishTime = (TextView) itemView.findViewById(R.id.publishTime);
            publisher = (TextView) itemView.findViewById(R.id.publisher);
            deleteButton = (ImageView) itemView.findViewById(R.id.deleteButton);
        }
    }
}

