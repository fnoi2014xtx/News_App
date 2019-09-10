package com.java.xtxnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TextRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> imageURLList;
    TextRecyclerViewAdapter(Context context, List<String> imageURLList){
        this.imageURLList = imageURLList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new NetImageViewHolder(inflater.inflate(R.layout.net_image_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NetImageViewHolder hd = (NetImageViewHolder)holder;
        hd.image.setImageURL(imageURLList.get(position));
    }
    //具体item数据等于pages*10，每页10条
    @Override
    public int getItemCount() {
        return imageURLList.size();
    }
    class NetImageViewHolder extends RecyclerView.ViewHolder {
        NetImageView image;
        NetImageViewHolder(View itemView) {
            super(itemView);
            image = (NetImageView)itemView.findViewById(R.id.photo);
        }
    }
}
