package com.org.competition.View.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Adapter.EventAdapter;
import com.org.competition.Base.BaseFragment;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Model.EventModel;
import com.org.competition.Model.EventPlayer;
import com.org.competition.Model.SportModel;
import com.org.competition.Model.SportParams;
import com.org.competition.R;
import com.org.competition.View.EventDetailView;
import com.org.competition.databinding.FragmentEventsBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventFragment extends BaseFragment {

    FragmentEventsBinding bind;
    List<EventModel> dataList;
    List<EventModel> filterList;
    EventAdapter adapter;
    int selected_key = -1;

    public EventFragment(){
        super();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind(inflater);
        return bind.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValues();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bind(LayoutInflater inflater){
        bind = FragmentEventsBinding.inflate(inflater);
        bind.eventList.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        filterList = new ArrayList<>();
        adapter = new EventAdapter(getContext(), filterList, 0);
        adapter.setListener(new EventAdapter.Listener() {
            @Override
            public void onDetail(EventModel model) {
                Intent intent = new Intent(getContext(), EventDetailView.class);
                intent.putExtra("event", model);
                startActivity(intent);
            }
        });
        bind.eventList.setAdapter(adapter);
    }
    private void initValues(){
        loadEvents();
        addSportIcons();
    }
    //load all events
    private void loadEvents(){
        dataList.clear();
        filterList.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.EVENT);
        dialogHelper.showProgressDialog();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                dataList.clear();
                filterList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventModel event = data.getValue(EventModel.class);
                        dataList.add(event);
                    }
                }
                filter();
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }

    private void addSportIcons(){
        bind.sportList.removeAllViews();
        LayoutInflater inflater = this.getLayoutInflater();
        for (final SportModel item : SportParams.SPORTS) {
            final View icon_layout = inflater.inflate(R.layout.layout_sport_icon, null);
            ImageView icon_view = icon_layout.findViewById(R.id.sport_icon);

            icon_view.setImageResource(SportParams.SPORT_ICONS.get(item.getKey()));

            icon_layout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selected_key = item.getKey();
                    deselectAll();
                    setSelectIcon(icon_view);
                    filter();
                }
            });
            bind.sportList.addView(icon_layout);
        }
    }

    private void setSelectIcon(ImageView view){
        view.setColorFilter(ContextCompat.getColor(getContext(), R.color.white_gray), android.graphics.PorterDuff.Mode.SRC_IN);
    }
    private void deselectAll(){
        int counter = bind.sportList.getChildCount();
        for(int i = 0; i < counter; i++){
            View view = bind.sportList.getChildAt(i);
            ImageView icon_view = view.findViewById(R.id.sport_icon);
            icon_view.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
        }
    }
    private void filter(){
        filterList.clear();
        if(selected_key < 0){
            filterList.addAll(dataList);
            return;
        }
        for(EventModel model : dataList){
            if(model.getSport().getKey() == selected_key){
                filterList.add(model);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
