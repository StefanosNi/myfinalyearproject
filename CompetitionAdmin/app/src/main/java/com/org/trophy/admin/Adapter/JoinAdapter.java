package com.org.trophy.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.org.trophy.admin.Model.JoinModel;
import com.org.trophy.admin.Model.SportModel;
import com.org.trophy.admin.Model.SportParams;
import com.org.trophy.admin.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.ViewHolder> {

    private List<JoinModel> localDataSet;
    private Listener mListener;
    private Context context;

    public interface Listener{
        //void onDetail(JoinModel model);
        void onApprove(JoinModel model);
        void onCancel(JoinModel model);
    }
    public void setListener(Listener listener){
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout data_area;
        private LinearLayout approve_btn;
        private LinearLayout cancel_btn;
        private ImageView sport_icon;
        private TextView sport_name;
        private CircleImageView user_image;
        private TextView username;
        private TextView about_us;

        public ViewHolder(View view) {
            super(view);
            data_area = view.findViewById(R.id.data_area);
            approve_btn = view.findViewById(R.id.approve_btn);
            cancel_btn = view.findViewById(R.id.cancel_btn);
            sport_icon = view.findViewById(R.id.sport_icon);
            sport_name = view.findViewById(R.id.sport_name);
            user_image = view.findViewById(R.id.user_profile);
            username = view.findViewById(R.id.username);
            about_us = view.findViewById(R.id.about_us);
        }
    }

    public JoinAdapter(Context context, List<JoinModel> dataSet) {
        this.context = context;
        localDataSet = dataSet;
    }

    @Override
    public JoinAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_join_item, viewGroup, false);
        return new JoinAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JoinAdapter.ViewHolder viewHolder, final int position) {
        JoinModel model = localDataSet.get(position);
        String user_profile_path = model.getUserImage();
        if(user_profile_path != null){
            Glide.with(context)
                    .load(user_profile_path)
                    .error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
                    .into(viewHolder.user_image);
        }
        int sport_key = model.getSport();
        String sportname = "";
        for(SportModel item : SportParams.SPORTS){
            if(item.getKey() == sport_key){
                sportname = item.getName();
            }
        }
        viewHolder.sport_name.setText(sportname);
        viewHolder.sport_icon.setImageResource(SportParams.SPORT_ICONS.get(sport_key));
        viewHolder.username.setText(model.getUsername());
        viewHolder.about_us.setText(model.getAboutUs());

//        viewHolder.data_area.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mListener != null){
//                    mListener.onDetail(model);
//                }
//            }
//        });
        viewHolder.approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.onApprove(model);
                }
            }
        });
        viewHolder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.onCancel(model);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
