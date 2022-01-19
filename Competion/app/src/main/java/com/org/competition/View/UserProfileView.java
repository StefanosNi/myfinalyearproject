package com.org.competition.View;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
import com.org.competition.Model.Follow;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.databinding.ActivityUserProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class UserProfileView extends BaseActivity {
    ActivityUserProfileBinding bind;
    String user_id;
    boolean isFollow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        user_id = getIntent().getStringExtra("user_id");
        bind.backBtn.setOnClickListener(this);
        bind.followBtn.setOnClickListener(this);
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
            bind.ranking.setText(getString(R.string.not_ranking));
            bind.rankingImage.setImageResource(R.drawable.ic_no_ranking);
        }
    }
    //load user information
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
    //get follow number of this user
    private void intFollow(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.FOLLOW);
        Query query = myRef.orderByChild("mainId").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int counter = 0;
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        Follow follow = item.getValue(Follow.class);
                        if(follow.getUserId().equals(currentUser.getUid())){
                            bind.followBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.round_gray_background));
                            isFollow = true;
                        }
                        counter++;
                    }
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
        }else if(view == bind.followBtn){
            if(isFollow) return;
            checkFollow();
        }
    }
    //check that it is followed already
    private void checkFollow(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.FOLLOW);
        Query query = myRef.orderByChild("mainId").equalTo(user_id);
        dialogHelper.showProgressDialog();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        Follow follow = item.getValue(Follow.class);
                        if(follow.getUserId().equals(currentUser.getUid())){
                            bind.followBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.round_gray_background));
                            isFollow = true;
                        }
                    }
                }
                if(!isFollow){
                    follow();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
                showMessage("Fail to follow !");
            }
        });
    }
    //follow user
    private void follow(){
        Follow model = new Follow();
        model.setMainId(user_id);
        model.setUserId(currentUser.getUid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.FOLLOW).push();
        dialogHelper.showProgressDialog();
        myRef.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to follow !");
                bind.followBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.round_gray_background));
                isFollow = true;
                try{
                    String follow_str = bind.follow.getText().toString().trim();
                    bind.follow.setText(String.valueOf(Integer.parseInt(follow_str) + 1));
                }catch (Exception e){

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to follow !");
                dialogHelper.closeDialog();
            }
        });
    }
}
