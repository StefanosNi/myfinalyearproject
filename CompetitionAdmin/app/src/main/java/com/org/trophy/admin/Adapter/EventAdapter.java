package com.org.trophy.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.org.trophy.admin.Model.EventModel;
import com.org.trophy.admin.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<EventModel> localDataSet;
    private Listener mListener;
    private Context context;
    private int type;

    public interface Listener{
        void onDetail(EventModel model);
        //void onUpdate(EventModel model);
    }
    public void setListener(Listener listener){
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout data_area;
        private TextView week_day;
        private TextView time_view;
        private TextView aa_view;
        private TextView title_view;
        private TextView start_time;
        private TextView address_view;

        public ViewHolder(View view) {
            super(view);
            data_area = view.findViewById(R.id.data_area);
            week_day = view.findViewById(R.id.week_day);
            time_view = view.findViewById(R.id.time_view);
            aa_view = view.findViewById(R.id.aa_view);
            title_view = view.findViewById(R.id.title);
            start_time = view.findViewById(R.id.start_date);
            address_view = view.findViewById(R.id.address);
        }
    }

    public EventAdapter(Context context, List<EventModel> dataSet, int type) {
        this.context = context;
        localDataSet = dataSet;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_event_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        EventModel model = localDataSet.get(position);
        String time = model.getTime();
        String hour = "";
        String aa = "";
        try{
            String[] splits = time.split("\\s+");
            hour = splits[0];
            aa = splits[1];
        }catch (Exception e){

        }
        viewHolder.week_day.setText(model.getWeekName());
        viewHolder.time_view.setText(hour);
        viewHolder.aa_view.setText(aa);
        viewHolder.title_view.setText(model.getTitle());
        viewHolder.start_time.setText(model.getStartAt());
        viewHolder.address_view.setText(model.getAddress());

        viewHolder.data_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.onDetail(model);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
