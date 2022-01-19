package com.org.competition.Base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.org.competition.Helper.DialogHelper;


public class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public Context context;
    public DialogHelper dialogHelper;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;

    public String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,};
    public final int PERMISSION_CODE = 4653;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        dialogHelper = new DialogHelper(context);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
    @Override
    public void onClick(View view){

    }
    @Override
    public void onBackPressed(){
        finish();
    }

    public void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public void showMessageShort(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public void startIntentAsFinishMode(Class<?> cls){
        Intent intent = new Intent(context, cls);
        startActivity(intent);
        finish();
    }
    public void startIntentAsCleanMode(Class<?> cls){
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void startIntentLiveMode(Class<?> cls){
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }
}
