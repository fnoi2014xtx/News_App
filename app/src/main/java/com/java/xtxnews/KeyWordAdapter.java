package com.java.xtxnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class KeyWordAdapter extends BaseAdapter {
    private Context context;
    public List<String> channelList;
    private TextView item_text;
    boolean isVisible = true;
    public int remove_position = -1;
    private boolean isUser = false;

    public KeyWordAdapter(Context context, List<String> channelList , boolean isUser) {
        this.context = context;
        this.channelList = channelList;
        this.isUser = isUser;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public String getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.keyword_item_layout, null);
        item_text = (TextView) view.findViewById(R.id.tv_des);
        String channel = getItem(position);
        item_text.setText(channel);
        if(isUser){
            if ((position == 0) || (position == 1)){
                item_text.setEnabled(false);
            }
        }
        if (!isVisible && (position == -1 + channelList.size())){
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
        }
        if(remove_position == position){
            item_text.setText("");
        }
        return view;
    }

    public List<String> getChannnelLst() {
        return channelList;
    }

    public void addItem(String channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    public void remove() {
        if(remove_position<0||remove_position>=channelList.size())
            return;
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();

    }
    public void setListDate(List<String> list) {
        channelList = list;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

}