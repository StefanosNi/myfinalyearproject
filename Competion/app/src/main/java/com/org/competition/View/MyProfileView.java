package com.org.competition.View;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.databinding.ActivityMyProfileBinding;
import com.org.competition.databinding.ActivityUserProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class MyProfileView extends BaseActivity {
    ActivityMyProfileBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        bind.backBtn.setOnClickListener(this);
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
        //bind.follow.setText(String.valueOf(user.getFollowing()));
        intFollow();
        int ranking = user.getRanking();
        if(ranking == 1){
            bind.ranking.setText(getString(R.string.bronze));
            bind.rankingImage.setImageResource(R.drawable.bronze);
        }else if(ranking == 2){
            bind.ranking.setText(getString(R.string.silver));
            bind.rankingImage.setImageResource(R.drawable.silver);
        }else if(ranking == 3){
            bind.ranking.setText(getString(R.string.gold));
            bind.rankingImage.setImageResource(R.drawable.gold);
        }else if(ranking == 4){
            bind.ranking.setText(getString(R.string.platinum));
            bind.rankingImage.setImageResource(R.drawable.platinum);
        }else if(ranking == 5){
            bind.ranking.setText(getString(R.string.diamond));
            bind.rankingImage.setImageResource(R.drawable.diamond);
        }else{
            bind.ranking.setText("No Ranking");
        }
    }
    private void loadUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.USER);
        Query query = myRef.orderByChild("id").equalTo(currentUser.getUid());
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
    private void intFollow(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.FOLLOW);
        Query query = myRef.orderByChild("mainId").equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int counter = 0;
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        counter++;
                    }
                    bind.follow.setText(String.valueOf(counter));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }
    }

    private void setRanking(int option){
        if(option == 1){
            bind.ranking.setText(getString(R.string.bronze));
            bind.rankingImage.setImageResource(R.drawable.bronze);
        }else if(option == 2){
            bind.ranking.setText(getString(R.string.silver));
            bind.rankingImage.setImageResource(R.drawable.silver);
        }else if(option == 3){
            bind.ranking.setText(getString(R.string.gold));
            bind.rankingImage.setImageResource(R.drawable.gold);
        }else if(option == 4){
            bind.ranking.setText(getString(R.string.platinum));
            bind.rankingImage.setImageResource(R.drawable.platinum);
        }else if(option == 5){
            bind.ranking.setText(getString(R.string.diamond));
            bind.rankingImage.setImageResource(R.drawable.diamond);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ranking", option);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.USER).child(currentUser.getUid());
        dialogHelper.showProgressDialog();
        myRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to update ranking !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to update ranking !");
                dialogHelper.closeDialog();
            }
        });
    }
}
