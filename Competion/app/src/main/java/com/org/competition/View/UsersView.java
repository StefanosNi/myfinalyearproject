package com.org.competition.View;

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
import com.org.competition.Adapter.UserAdapter;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Model.User;
import com.org.competition.databinding.ActivityUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersView extends BaseActivity {
    ActivityUsersBinding bind;
    UserAdapter adapter;
    List<User> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        dataList = new ArrayList<>();
        bind.userList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new UserAdapter(context, dataList, 0);
        adapter.setListener(new UserAdapter.Listener() {
            @Override
            public void onSelect(User model) {
                Intent intent = new Intent(context, UserProfileView.class);
                intent.putExtra("user_id", model.getId());
                startActivity(intent);
            }
        });
        bind.userList.setAdapter(adapter);
        bind.backBtn.setOnClickListener(this);
        loadUsers();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }
    }
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
                        if(!user.getId().equals(currentUser.getUid())){
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
}
