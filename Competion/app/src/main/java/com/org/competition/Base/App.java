package com.org.competition.Base;

import android.app.Application;

import com.org.competition.Model.SportParams;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    private void init(){
        SportParams.initSports();
    }
}
