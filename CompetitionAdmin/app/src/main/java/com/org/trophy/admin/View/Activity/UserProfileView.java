package com.org.trophy.admin.View.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Iterators;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.trophy.admin.Adapter.RankingAdapter;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Model.EventPlayer;
import com.org.trophy.admin.Model.SportParams;
import com.org.trophy.admin.Model.User;
import com.org.trophy.admin.R;
import com.org.trophy.admin.View.Dialog.RankingDialog;
import com.org.trophy.admin.databinding.ActivityUserProfileBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileView extends BaseActivity {
    ActivityUserProfileBinding bind;
    String user_id;
    String event_id;
    RankingAdapter ranking_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        user_id = getIntent().getStringExtra("user_id");
        event_id = getIntent().getStringExtra("event_id");

        bind.backBtn.setOnClickListener(this);
        bind.updateUserBtn.setOnClickListener(this);
        bind.removePlayerBtn.setOnClickListener(this);
        loadUser();
    }
    private void initView(User user){
        if(user == null) return;
        if(user.getImage() != null){
            Glide.with(context)
                    .load(user.getImage())
                    .error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
                    .into(bind.profileImage);
        }
        String gender = user.getGender();
        if(gender != null){
            if(gender.equals("male")){
                bind.maleOption.setChecked(true);
            }else if(gender.equals("female")){
                bind.femaleOption.setChecked(true);
            }
        }
        bind.username.setText(user.getName());
        bind.email.setText(user.getEmail());
        bind.phone.setText(user.getPhone());
        bind.birthday.setText(user.getBirth());
        bind.wins.setText(String.valueOf(user.getWins()));
        ranking_adapter = new RankingAdapter(context, SportParams.RANKING);
        bind.ranking.setAdapter(ranking_adapter);

        //bind.follow.setText(String.valueOf(user.getFollowing()));
        intFollow();
        int ranking = user.getRanking();
        bind.ranking.setSelection(ranking);
    }

    //get user details
    private void loadUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.USER);
        Query query = myRef.orderByChild("id").equalTo(user_id);
        dialogHelper.showProgressDialog();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                if(dataSnapshot.exists()){
                    User user = null;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        user = data.getValue(User.class);
                    }
                    initView(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //get follow user's number
    private void intFollow(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.FOLLOW);
        Query query = myRef.orderByChild("mainId").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int counter = Iterators.size(dataSnapshot.getChildren().iterator());
                    bind.follow.setText(String.valueOf(counter));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }else if(view == bind.updateUserBtn){
            updateUser();
        }else if(view == bind.removePlayerBtn){
            removePlayer();
        }
    }
    //update user information
    private void updateUser(){
        String wins_str = bind.wins.getText().toString().trim();
        int wins = 0;
        if(wins_str.isEmpty()){
            wins = 0;
        }else{
            wins = (int)Double.parseDouble(wins_str);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ranking", bind.ranking.getSelectedItemPosition());
        map.put("wins", wins);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.USER).child(user_id);
        dialogHelper.showProgressDialog();
        myRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to update user !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to update user !");
                dialogHelper.closeDialog();
            }
        });
    }
    //remove this user in event
    private void removePlayer(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS);
        Query query = myRef.orderByChild("eventId").equalTo(event_id);
        dialogHelper.showProgressDialog();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventPlayer player = data.getValue(EventPlayer.class);
                        if(player.getUserId().equals(user_id)){
                            //remove player from event
                            data.getRef().removeValue();
                            showMessage("Success to remove player.");
                            finish();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
}
