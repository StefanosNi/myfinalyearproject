package com.org.competition.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.org.competition.Model.User;
import com.org.competition.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> localDataSet;
    private Listener mListener;
    private Context context;
    private int type;

    public interface Listener{
        void onSelect(User model);
        //void onUpdate(EventModel model);
    }
    public void setListener(Listener listener){
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout data_area;
        private CircleImageView user_profile;
        private TextView name;
        private TextView birthday;
        private TextView gender;

        public ViewHolder(View view) {
            super(view);
            data_area = view.findViewById(R.id.data_area);
            user_profile = view.findViewById(R.id.user_profile);
            name = view.findViewById(R.id.name);
            birthday = view.findViewById(R.id.birthday);
            gender = view.findViewById(R.id.gender);
        }
    }

    public UserAdapter(Context context, List<User> dataSet, int type) {
        this.context = context;
        localDataSet = dataSet;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        User model = localDataSet.get(position);
        String user_profile_path = model.getImage();
        if(user_profile_path != null){
            Glide.with(context)
                    .load(user_profile_path)
                    .error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
                    .into(viewHolder.user_profile);
        }

        viewHolder.name.setText(model.getName());
        viewHolder.birthday.setText(model.getBirth());
        viewHolder.gender.setText(model.getGender());
        viewHolder.data_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onSelect(model);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
