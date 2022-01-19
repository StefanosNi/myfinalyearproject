package com.org.trophy.admin.View.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Helper.Utils;
import com.org.trophy.admin.databinding.ActivitySplashBinding;

public class SplashView extends BaseActivity {

    ActivitySplashBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        start();
    }

    private void start() {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                checkPermission();
            }
        }.start();
    }
    private void LoadHome(){
        startIntentAsCleanMode(HomeView.class);
//        if(currentUser == null){
//            //startIntentAsFinishMode(LoginView.class);
//        }else{
//            //startIntentAsFinishMode(HomeView.class);
//        }
    }
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        }else {
            checkLocationEnabled();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                checkLocationEnabled();
            }else{
                Toast.makeText(this, "Permission denied !", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    private void checkLocationEnabled(){
        if(!Utils.isLocationEnabled(context)){
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            finish();
        }else{
            LoadHome();
        }
    }
}
