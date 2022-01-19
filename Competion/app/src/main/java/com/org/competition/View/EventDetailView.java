package com.org.competition.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Helper.DialogHelper;
import com.org.competition.Model.EventModel;
import com.org.competition.Model.EventPlayer;
import com.org.competition.Model.JoinModel;
import com.org.competition.Model.SportParams;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.databinding.ActivityEventDetailBinding;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventDetailView extends BaseActivity implements OnMapReadyCallback {
    ActivityEventDetailBinding bind;
    EventModel event;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        event = (EventModel)getIntent().getSerializableExtra("event");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //show event detail
        bind.dayView.setText(event.getTime() + "  |  " + event.getDuration() + "\n" + "Every " + event.getOnceWeek());
        bind.deadlineView.setText("Deadline by " + event.getDeadline() + "\n" + "Event start at " + event.getStartAt());
        bind.sport.setText(event.getSport().getName());
        bind.sportIcon.setImageResource(SportParams.SPORT_ICONS.get(event.getSport().getKey()));
        bind.address.setText(event.getAddress());
        bind.price.setText(event.getPrice());

        bind.backBtn.setOnClickListener(this);
        bind.joinBtn.setOnClickListener(this);
        bind.cancelBtn.setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadPlayers();
        checkExist();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }else if(view == bind.cancelBtn){
            finish();
        }else if(view == bind.joinBtn){
            join();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            mMap = googleMap;
            mMap.clear();
            LatLng current = new LatLng(event.getLat(), event.getLon());
            mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11.5f));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //load all event players
    private void loadPlayers(){
        bind.playerList.removeAllViews();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS);
        Query query = myRef.orderByChild("eventId").equalTo(event.getId());
        dialogHelper.showProgressDialog();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                bind.playerList.removeAllViews();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventPlayer player = data.getValue(EventPlayer.class);
                        addPlayer(player);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //add players to event
    private void addPlayer(EventPlayer player){
        LayoutInflater inflater = this.getLayoutInflater();
        final View player_layout = inflater.inflate(R.layout.layout_player1, null);

        CircleImageView user_profile = player_layout.findViewById(R.id.user_profile);
        TextView name_view = player_layout.findViewById(R.id.name);
        String user_profile_url = player.getUserImage();
        if(user_profile_url != null){
            Glide.with(context)
                    .load(player.getUserImage())
                    .error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
                    .into(user_profile);
        }
        name_view.setText(player.getUsername());
        bind.playerList.addView(player_layout);
        bind.playerArea.setVisibility(View.VISIBLE);
    }
    private void checkExist(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS);
        Query query = myRef.orderByChild("userId").equalTo(currentUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    checkJoin();
                }else{
                    boolean exist = false;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventPlayer player = data.getValue(EventPlayer.class);
                        if(player.getEventId().equals(event.getId())){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        checkJoin();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //check that user send already join request to this event
    private void checkJoin(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.JOIN);
        Query query = myRef.orderByChild("userId").equalTo(currentUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    bind.joinArea.setVisibility(View.VISIBLE);
                    bind.aboutArea.setVisibility(View.VISIBLE);
                }else{
                    boolean exist = false;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        JoinModel join = data.getValue(JoinModel.class);
                        if(join.getEventId().equals(event.getId())){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        bind.joinArea.setVisibility(View.VISIBLE);
                        bind.aboutArea.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //join event
    private void join(){
        JoinModel model = new JoinModel();
        model.setEventId(event.getId());
        model.setEventTitle(event.getTitle());
        model.setSport(event.getSport().getKey());
        model.setUserId(currentUser.getUid());
        model.setAboutUs(bind.aboutUs.getText().toString().trim());

        DialogHelper.showMessageDialog(context, "", "Are you sure to join to this event now ?", new DialogHelper.MyDialogListener() {
            @Override
            public void onClickOk() {
                joinEvent(model);
            }
            @Override
            public void onClickNo() {

            }
        });
    }
    //join event
    private void joinEvent(JoinModel model){
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.USER);
        Query query = mDatabaseRef.orderByChild("id").equalTo(currentUser.getUid());
        dialogHelper.showProgressDialog();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = null;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        user = data.getValue(User.class);
                    }
                    if(user != null){
                        model.setUsername(user.getName());
                        model.setUserImage(user.getImage());
                        saveJoin(model);
                    }else{
                        dialogHelper.closeDialog();
                    }
                }else{
                    dialogHelper.closeDialog();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //save join request to firebase
    private void saveJoin(JoinModel model){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.JOIN).push();
        model.setId(myRef.getKey());
        // add post data to firebase database
        myRef.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Your request is pending now !");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to join !");
                dialogHelper.closeDialog();
            }
        });
    }
}
