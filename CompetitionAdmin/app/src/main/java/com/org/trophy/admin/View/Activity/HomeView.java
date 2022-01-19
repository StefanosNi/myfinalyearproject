package com.org.trophy.admin.View.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.google.common.collect.Iterators;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Model.EventModel;
import com.org.trophy.admin.databinding.ActivityHomeBinding;

import java.util.stream.StreamSupport;

public class HomeView extends BaseActivity {

    ActivityHomeBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //connect design layout to source
        bind = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }

    private void initView(){
        // add touch event to buttons
        bind.addEvent.setOnClickListener(this);
        bind.allEvents.setOnClickListener(this);
        bind.requestBtn.setOnClickListener(this);
        bind.joinUsers.setOnClickListener(this);
        counterJoin();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.addEvent){
            //go to event add view
            startIntentLiveMode(EventAddView.class);
        }else if(view == bind.allEvents){
            //go to events view
            startIntentLiveMode(EventsView.class);
        }else if(view == bind.requestBtn){
            //go to user's join request view
            startIntentLiveMode(JoinListView.class);
        }else if(view == bind.joinUsers){
            //go to user's join request view
            startIntentLiveMode(JoinListView.class);
        }
    }
    //get user's join request number from firebase
    private void counterJoin(){
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.JOIN);
        dialogHelper.showProgressDialog();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                //success request and get user's join request number
                if(dataSnapshot.exists()){
                    int counter = Iterators.size(dataSnapshot.getChildren().iterator());
                    //show request number to red badge view
                    bind.requestNum.setNumber(counter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
}
