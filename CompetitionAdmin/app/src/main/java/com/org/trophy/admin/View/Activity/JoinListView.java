package com.org.trophy.admin.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.trophy.admin.Adapter.JoinAdapter;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Model.EventPlayer;
import com.org.trophy.admin.Model.JoinModel;
import com.org.trophy.admin.databinding.ActivityJoinListBinding;

import java.util.ArrayList;
import java.util.List;

public class JoinListView extends BaseActivity {
    ActivityJoinListBinding bind;
    List<JoinModel> dataList;
    JoinAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityJoinListBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        bind.backBtn.setOnClickListener(this);
        dataList = new ArrayList<>();
        bind.joinList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new JoinAdapter(context, dataList);
        adapter.setListener(new JoinAdapter.Listener() {
            @Override
            public void onApprove(JoinModel model) {
                checkPlayer(model);
            }
            @Override
            public void onCancel(JoinModel model) {
                removeJoin(model);
            }
        });
        bind.joinList.setAdapter(adapter);
        load();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }
    }
    //get all join request
    private void load(){
        dataList.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.JOIN);
        dialogHelper.showProgressDialog();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        JoinModel model = data.getValue(JoinModel.class);
                        dataList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }
                dialogHelper.closeDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //check user is already joined to event and approve it or remove
    private void checkPlayer(JoinModel model){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS);
        Query query = myRef.orderByChild("eventId").equalTo(model.getEventId());
        dialogHelper.showProgressDialog();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boolean exist = false;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventPlayer player = data.getValue(EventPlayer.class);
                        if(player.getUserId().equals(model.getUserId())){
                            //if user join this event already , it is rejected
                            removeJoin(model);
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        //if this is new user, it is approved
                        approve(model);
                    }else{
                        dialogHelper.closeDialog();
                    }
                }else{
                    approve(model);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //approve user and save player to event
    private void approve(JoinModel model){
        EventPlayer player = new EventPlayer();
        player.setEventId(model.getEventId());
        player.setUserId(model.getUserId());
        player.setUsername(model.getUsername());
        player.setUserImage(model.getUserImage());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS).push();
        dialogHelper.showProgressDialog();
        myRef.setValue(player).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to add player !");
                removeJoin(model);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to add player !");
                dialogHelper.closeDialog();
            }
        });
    }
    private void removeJoin(JoinModel model){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(Firebase.JOIN).child(model.getId()).removeValue();
        dataList.remove(model);
        adapter.notifyDataSetChanged();
    }
}
