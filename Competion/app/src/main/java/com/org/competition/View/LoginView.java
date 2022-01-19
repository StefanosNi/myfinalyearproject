package com.org.competition.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Helper.StringHelper;
import com.org.competition.databinding.ActivityLoginBinding;


public class LoginView extends BaseActivity {

    ActivityLoginBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        bind.loginBtn.setOnClickListener(this);
        bind.registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == bind.loginBtn){
            login();
        }else if(view == bind.registerBtn){
            startIntentLiveMode(RegisterView.class);
        }
    }
    private void login(){
        String email = bind.userEmail.getText().toString().trim();
        String pass = bind.userPassword.getText().toString().trim();
        //validate user input
        if(StringHelper.isEmpty(email)){
            bind.userEmail.setError("Please enter email.");
            return;
        }
        if(StringHelper.isEmpty(pass)){
            bind.userPassword.setError("Please enter password.");
            return;
        }
        dialogHelper.showProgressDialog();
        //sign in firebase auth
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialogHelper.closeDialog();
                if (task.isSuccessful()) {
                    currentUser = mAuth.getCurrentUser();
                    startIntentAsCleanMode(HomeView.class);
                } else {
                    showMessage(task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to login !");
                dialogHelper.closeDialog();
            }
        });
    }
}
