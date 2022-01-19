package com.org.competition.View.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Base.BaseFragment;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.databinding.FragmentProfileBinding;

public class ProfileFragment extends BaseFragment {
    FragmentProfileBinding bind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind(inflater);
        return bind.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            loadUser();
        }
    }
    private void bind(LayoutInflater inflater){
        bind = FragmentProfileBinding.inflate(inflater);
    }

    private void initView(User user){
        if(user == null) return;
        if(user.getImage() != null){
            Glide.with(getContext())
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
            bind.ranking.setText(getString(R.string.not_ranking));
            bind.rankingImage.setImageResource(R.drawable.ic_no_ranking);
        }
    }
    //get user information
    private void loadUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.USER);
        Query query = myRef.orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = null;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        user = data.getValue(User.class);
                    }
                    //show all information
                    initView(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void intFollow(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.FOLLOW);
        Query query = myRef.orderByChild("mainId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    }
}
