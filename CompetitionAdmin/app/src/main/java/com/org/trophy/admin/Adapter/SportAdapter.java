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

import com.bumptech.glide.Glide;
import com.org.trophy.admin.Model.SportModel;
import com.org.trophy.admin.Model.SportParams;
import com.org.trophy.admin.R;

import java.io.InputStream;
import java.util.List;

public class SportAdapter extends BaseAdapter {
    Context context;
    List<SportModel> items;
    LayoutInflater inflter;

    public SportAdapter(Context applicationContext, List<SportModel> items) {
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
        ViewHolder mHolder;
        final SportModel item = (SportModel) getItem(i);
        if(view == null){
            view = inflter.inflate(R.layout.layout_sport_item, viewGroup, false);
            mHolder = new ViewHolder();
            mHolder.icon = view.findViewById(R.id.icon);
            mHolder.name = view.findViewById(R.id.text);
            view.setTag(mHolder);
        }else{
            mHolder = (ViewHolder) view.getTag();
        }
        mHolder.name.setText(item.getName());

        //InputStream imageStream = context.getResources().openRawResource(item.getImage());
        //Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        mHolder.icon.setImageResource(SportParams.SPORT_ICONS.get(item.getKey()));
        return view;
    }
    class ViewHolder{
        ImageView icon;
        TextView name;
    }
}