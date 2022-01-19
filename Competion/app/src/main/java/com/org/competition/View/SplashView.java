package com.org.competition.View;

import android.os.Bundle;
import android.os.CountDownTimer;

import com.org.competition.Base.BaseActivity;
import com.org.competition.R;
import com.org.competition.databinding.ActivitySplashBinding;

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
                LoadHome();
            }
        }.start();
    }
    private void LoadHome(){
        if(currentUser == null){
            startIntentAsFinishMode(LoginView.class);
        }else{
            startIntentAsFinishMode(HomeView.class);
        }
    }
}
