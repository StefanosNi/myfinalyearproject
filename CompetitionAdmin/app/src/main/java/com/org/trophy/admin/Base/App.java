package com.org.trophy.admin.Base;

import android.app.Application;

import com.org.trophy.admin.Model.SportParams;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    private void init(){
        SportParams.initSports(this);
    }
}
