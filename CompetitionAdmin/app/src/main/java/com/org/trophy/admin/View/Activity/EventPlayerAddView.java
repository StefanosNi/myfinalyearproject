package com.org.trophy.admin.View.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.trophy.admin.Adapter.UserAdapter;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Helper.DialogHelper;
import com.org.trophy.admin.Model.EventModel;
import com.org.trophy.admin.Model.EventPlayer;
import com.org.trophy.admin.Model.User;
import com.org.trophy.admin.databinding.ActivityAddPlayerBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EventPlayerAddView extends BaseActivity {
    ActivityAddPlayerBinding bind;
    UserAdapter adapter;
    List<User> dataList;
    EventModel event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityAddPlayerBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        event = (EventModel)getIntent().getSerializableExtra("event") ;
        dataList = new ArrayList<>();
        bind.playerList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new UserAdapter(context, dataList, 0);
        adapter.setListener(new UserAdapter.Listener() {
            @Override
            public void onSelect(User model) {
                addPlayer(model);
            }
        });
        bind.playerList.setAdapter(adapter);
        bind.backBtn.setOnClickListener(this);
        load();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }
    }
    //get all event players
    private void load(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS);
        Query query = myRef.orderByChild("eventId").equalTo(event.getId());
        dialogHelper.showProgressDialog();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> players = new ArrayList<>();
                if(dataSnapshot.exists()){
                    //get all event users
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventPlayer player = data.getValue(EventPlayer.class);
                        players.add(player.getUserId());
                    }
                }
                //load available users
                loadUsers(players);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //load available users
    //it get all players from table and check that any user is already joined this event
    //If he is not join yet, it is available to add this event now
    private void loadUsers(List<String> players){
        dataList.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.USER);
        dialogHelper.showProgressDialog();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get all users
                dialogHelper.closeDialog();
                dataList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        User user = data.getValue(User.class);
                        String user_id = user.getId();
                        if(user_id != null){
                            //if this user is not exist in event, it is available to add now
                            if(!players.contains(user_id)){
                                dataList.add(user);
                            }
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
    private void addPlayer(User user){
        DialogHelper.showMessageDialog(context, "", "Are you sure to add this user ?", new DialogHelper.MyDialogListener() {
            @Override
            public void onClickOk() {
                savePlayer(user);
            }
            @Override
            public void onClickNo() {

            }
        });
    }
    //save selected player to firebase
    private void savePlayer(User user){
        //initialize event player
        EventPlayer player = new EventPlayer();
        player.setEventId(event.getId());
        player.setUserId(user.getId());
        player.setUsername(user.getName());
        player.setUserImage(user.getImage());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS).push();
        //save player to firebase
        dialogHelper.showProgressDialog();
        myRef.setValue(player).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to add player !");
                dataList.remove(user);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to add player !");
                dialogHelper.closeDialog();
            }
        });
    }
}
