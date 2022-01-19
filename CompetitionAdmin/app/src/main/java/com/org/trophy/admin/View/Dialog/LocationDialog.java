package com.org.trophy.admin.View.Dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.org.trophy.admin.Base.BaseDialog;
import com.org.trophy.admin.Helper.HttpHelper;
import com.org.trophy.admin.Helper.HttpListener;
import com.org.trophy.admin.Helper.LocationHelper;
import com.org.trophy.admin.R;
import com.org.trophy.admin.databinding.DialogMapBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class LocationDialog extends BaseDialog implements OnMapReadyCallback, HttpListener {
    DialogMapBinding bind;
    LocationListener mListener;
    GoogleMap mMap;
    LatLng current;
    String address = "Unknown";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bind();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(bind.getRoot());
        return builder.create();
    }
    private void bind(){
        bind = DialogMapBinding.inflate(Objects.requireNonNull(getActivity()).getLayoutInflater());
        bind.closeBtn.setOnClickListener(this);
        bind.selectLocation.setOnClickListener(this);
        assert getFragmentManager() != null;
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }
    public interface LocationListener{
        void onLocation(String address, LatLng location);
    }
    public void setListener(LocationListener listener){
        this.mListener = listener;
    }
    @Override
    public void onClick(View view){
        if(view == bind.closeBtn){
            dismiss();
        }else if(view == bind.selectLocation){
            if(mListener != null){
                mListener.onLocation(address, current);
            }
            dismiss();
        }
    }
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

            getAddress(current.latitude, current.longitude);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng clickCoords) {
                    mMap.clear();
                    current = clickCoords;
                    mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11.5f));
                    getAddress(current.latitude, current.longitude);
                }
            });
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        assert getFragmentManager() != null;
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        assert fragment != null;
        ft.remove(fragment);
        ft.commit();
    }
    private void getAddress(double lat, double lon) {
        if(!HttpHelper.isNetworkAvailable(getContext())){
            showMessage(getString(R.string.no_network));
            return;
        }
        final HashMap<String, String> params = new HashMap<>();
        String geo_url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&key=" + getString(R.string.google_maps_key);
        HttpHelper helper = new HttpHelper(geo_url, HttpHelper.TYPE.GET, this, params, 1, (Object) params);
        helper.execute();
        bind.progress.setVisibility(View.VISIBLE);
    }
    private void parseAddress(String response){
        try {
            bind.progress.setVisibility(View.GONE);
            JSONObject resultObj = new JSONObject(response);
            String status = resultObj.getString("status");
            if (status.equalsIgnoreCase("ok")) {
                JSONArray results = new JSONArray(resultObj.getString("results"));
                if (results.length() >= 1) {
                    JSONObject address_obj = (JSONObject) results.get(0);
                    address = address_obj.getString("formatted_address");
                    bind.address.setText(address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResponse(String response, int code, Object... objects) {
        if(code == 1){
            parseAddress(response);
        }
    }
}
