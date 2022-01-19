package com.org.trophy.admin.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.trophy.admin.Model.RankingModel;
import com.org.trophy.admin.R;

import java.io.InputStream;
import java.util.List;

public class RankingAdapter extends BaseAdapter {
    Context context;
    List<RankingModel> items;
    LayoutInflater inflter;

    public RankingAdapter(Context applicationContext, List<RankingModel> items) {
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
        RankingAdapter.ViewHolder mHolder;
        final RankingModel item = (RankingModel) getItem(i);
        if(view == null){
            view = inflter.inflate(R.layout.layout_ranking_item, viewGroup, false);
            mHolder = new RankingAdapter.ViewHolder();
            mHolder.icon = view.findViewById(R.id.icon);
            mHolder.name = view.findViewById(R.id.text);
            view.setTag(mHolder);
        }else{
            mHolder = (RankingAdapter.ViewHolder) view.getTag();
        }
        mHolder.name.setText(item.getName());

        mHolder.icon.setImageResource(item.getIcon());
        return view;
    }
    class ViewHolder{
        ImageView icon;
        TextView name;
    }
}