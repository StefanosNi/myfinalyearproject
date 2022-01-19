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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Adapter.UserAdapter;
import com.org.competition.Base.BaseFragment;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.View.UserProfileView;
import com.org.competition.databinding.FragmentUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends BaseFragment {
    FragmentUsersBinding bind;
    UserAdapter adapter;
    List<User> dataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind(inflater);
        return bind.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
        }
    }
    private void bind(LayoutInflater inflater){
        bind = FragmentUsersBinding.inflate(inflater);
    }

    private void initView(){
        dataList = new ArrayList<>();
        bind.userList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(), dataList, 0);
        adapter.setListener(new UserAdapter.Listener() {
            @Override
            public void onSelect(User model) {
                Intent intent = new Intent(getContext(), UserProfileView.class);
                intent.putExtra("user_id", model.getId());
                startActivity(intent);
            }
        });
        bind.userList.setAdapter(adapter);
        loadUsers();
    }
    //get all users
    private void loadUsers(){
        dataList.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.USER);
        dialogHelper.showProgressDialog();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                dataList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        User user = data.getValue(User.class);
                        if(!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            dataList.add(user);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
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
