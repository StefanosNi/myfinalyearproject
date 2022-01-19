package com.org.competition.View.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Adapter.EventAdapter;
import com.org.competition.Base.BaseFragment;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Helper.LocationHelper;
import com.org.competition.Helper.Utils;
import com.org.competition.Model.EventModel;
import com.org.competition.Model.EventPlayer;
import com.org.competition.Model.SportParams;
import com.org.competition.R;
import com.org.competition.databinding.FragmentMapBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {
    FragmentMapBinding bind;
    GoogleMap mMap;
    LatLng current;
    List<EventModel> dataList;

    public MapFragment(){
        super();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind(inflater);
        return bind.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMap();
    }
    private void bind(LayoutInflater inflater){
        bind = FragmentMapBinding.inflate(inflater);
    }
    private void loadMap(){
        assert getFragmentManager() != null;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }
    //load map and events
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            LocationHelper location_helper = new LocationHelper(getContext());
            mMap = googleMap;
            mMap.clear();
            Location location = location_helper.getLocation();
            if(location != null){
                current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
            }else{
                current = new LatLng(53.472, -2.244);
                mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11.5f));
            //load events on map
            loadEvents();

            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //get all events and add them on map
    private void loadEvents(){
        dataList = new ArrayList<>();
        mMap.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Firebase.EVENT);
        dialogHelper.showProgressDialog();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogHelper.closeDialog();
                dataList.clear();
                mMap.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        EventModel event = data.getValue(EventModel.class);
                        dataList.add(event);
                        addEventMarker(event);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHelper.closeDialog();
            }
        });
    }
    //add event marker
    private void addEventMarker(EventModel model){
        LatLng latlng = new LatLng(model.getLat(), model.getLon());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(model.getTitle())
                .icon(bitmapDescriptorFromVector(model)));
        marker.setTag(model);
    }
    //create custom marker with sport icon
    private BitmapDescriptor bitmapDescriptorFromVector(EventModel model) {
        InputStream imageStream = getResources().openRawResource(SportParams.SPORT_ICONS.get(model.getSport().getKey()));
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap = Utils.getResizedBitmap(mutableBitmap, 100, 100);
        //change bitmap color
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.blue), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);

        return BitmapDescriptorFactory.fromBitmap(mutableBitmap);
    }
}
