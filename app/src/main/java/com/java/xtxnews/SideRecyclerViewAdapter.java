package com.java.xtxnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.nio.channels.Channel;
import java.util.List;

public class SideRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> categories;
    private ClickInterface clickInterface;
    public SideRecyclerViewAdapter(Context context, List<String> categories){
        this.categories = categories;
        this.context = context;
    }

    public void setOnClick(ClickInterface clickInterface){
        this.clickInterface = clickInterface;
    }

    public interface ClickInterface{
        void onItemClick(Context context,String category);
    }


    @Override
    public int getItemViewType(int position) {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new SideRecyclerViewAdapter.ChannelViewHolder(inflater.inflate(R.layout.side_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ChannelViewHolder hd = (ChannelViewHolder)holder;
        hd.tv.setText(categories.get(position));
        hd.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickInterface!=null){
                    clickInterface.onItemClick(context,categories.get(position));
                }
            }
        });
    }
    //具体item数据等于pages*10，每页10条
    @Override
    public int getItemCount() {
        return categories.size();
    }
    class ChannelViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ChannelViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.item);
        }
    }

}
