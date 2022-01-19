package com.org.trophy.admin.View.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Helper.DialogHelper;
import com.org.trophy.admin.Helper.LocationHelper;
import com.org.trophy.admin.Model.EventModel;
import com.org.trophy.admin.Model.EventPlayer;
import com.org.trophy.admin.Model.SportParams;
import com.org.trophy.admin.R;
import com.org.trophy.admin.databinding.ActivityEventDetailBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        //show event information
        bind.dayView.setText(event.getTime() + "  |  " + event.getDuration() + "\n" + "Every " + event.getOnceWeek());
        bind.deadlineView.setText("Deadline by " + event.getDeadline() + "\n" + "Event start at " + event.getStartAt());
        bind.sport.setText(event.getSport().getName());
        bind.sportIcon.setImageResource(SportParams.SPORT_ICONS.get(event.getSport().getKey()));
        bind.address.setText(event.getAddress());
        bind.price.setText(event.getPrice());

        bind.backBtn.setOnClickListener(this);
        bind.addPlayer.setOnClickListener(this);
        bind.removeEvent.setOnClickListener(this);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //load players whenever resume view
        loadPlayers();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.backBtn){
            finish();
        }else if(view == bind.addPlayer){
            Intent intent = new Intent(context, EventPlayerAddView.class);
            intent.putExtra("event", event);
            startActivity(intent);
        }else if(view == bind.removeEvent){
            removeEventConfirm();
        }
    }
    //load google map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            //get google map
            mMap = googleMap;
            mMap.clear();
            //add marker on google map of event location
            LatLng current = new LatLng(event.getLat(), event.getLon());
            mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            //move google map to location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11.5f));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //load players
    private void loadPlayers(){
        bind.playerList.removeAllViews();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.PLAYERS);
        //query firebase table with event id to get all joined players
        Query query = myRef.orderByChild("eventId").equalTo(event.getId());
        dialogHelper.showProgressDialog();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if success, it show all players on view
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
    //add player on view
    private void addPlayer(EventPlayer player){
        LayoutInflater inflater = this.getLayoutInflater();
        final View player_layout = inflater.inflate(R.layout.layout_player1, null);

        LinearLayout area = player_layout.findViewById(R.id.data_area);
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
        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileView.class);
                intent.putExtra("user_id", player.getUserId());
                intent.putExtra("event_id", event.getId());
                startActivity(intent);
            }
        });
    }
    private void removeEventConfirm(){
        DialogHelper.showMessageDialog(context, "", "Are you sure to remove event now ?", new DialogHelper.MyDialogListener() {
            @Override
            public void onClickOk() {
                removeEvent();
            }
            @Override
            public void onClickNo() {

            }
        });
    }
    //remove event
    private void removeEvent(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //remove event
        database.getReference(Firebase.EVENT).child(event.getId()).removeValue();
        //remove all players
        dialogHelper.showProgressDialog();
        database.getReference(Firebase.PLAYERS).orderByChild("eventId").equalTo(event.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        data.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });

        //remove all players
        dialogHelper.showProgressDialog();
        database.getReference(Firebase.JOIN).orderByChild("eventId").equalTo(event.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        data.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
        finish();
    }
}
