package com.org.trophy.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.org.trophy.admin.R;

import java.util.List;

public class SpinAdapter extends BaseAdapter {
    Context context;
    List<String> items;
    LayoutInflater inflter;

    public SpinAdapter(Context applicationContext, List<String> items) {
        this.context = applicationContext;
        this.items = items;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SpinAdapter.ViewHolder mHolder;
        final String item = (String) getItem(i);
        if(view == null){
            view = inflter.inflate(R.layout.layout_spiner_item, viewGroup, false);
            mHolder = new SpinAdapter.ViewHolder();
            mHolder.item_view = view.findViewById(R.id.text);
            view.setTag(mHolder);
        }else{
            mHolder = (SpinAdapter.ViewHolder) view.getTag();
        }
        mHolder.item_view.setText(item);
        return view;
    }
    class ViewHolder{
        TextView item_view;
    }
}