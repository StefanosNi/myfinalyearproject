package com.org.trophy.admin.View.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.org.trophy.admin.Adapter.EventAdapter;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Model.EventModel;
import com.org.trophy.admin.Model.SportModel;
import com.org.trophy.admin.Model.SportParams;
import com.org.trophy.admin.R;
import com.org.trophy.admin.databinding.ActivityEventsBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EventsView extends BaseActivity {
    ActivityEventsBinding bind;
    EventAdapter adapter;
    List<EventModel> dataList;
    List<EventModel> filterList;
    int selected_key = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        bind.backBtn.setOnClickListener(this);
        dataList = new ArrayList<>();
        filterList = new ArrayList<>();
        //initialize event recycle view and set adapter
        bind.eventList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new EventAdapter(context, filterList, 0);
        adapter.setListener(new EventAdapter.Listener() {
            @Override
            public void onDetail(EventModel model) {
                Intent intent = new Intent(context, EventDetailView.class);
                intent.putExtra("event", model);
                startActivity(intent);
            }
        });
        bind.eventList.setAdapter(adapter);
        addSportIcons();
        loadEvents();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }
    }
    //load sports from firebase
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
    //add sport icons on top to choose sport category
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
    //select sport category
    private void setSelectIcon(ImageView view){
        view.setColorFilter(ContextCompat.getColor(context, R.color.white_gray), android.graphics.PorterDuff.Mode.SRC_IN);
    }
    //deselect all icons
    private void deselectAll(){
        int counter = bind.sportList.getChildCount();
        for(int i = 0; i < counter; i++){
            View view = bind.sportList.getChildAt(i);
            ImageView icon_view = view.findViewById(R.id.sport_icon);
            icon_view.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
    }
    //filter sports by category
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
