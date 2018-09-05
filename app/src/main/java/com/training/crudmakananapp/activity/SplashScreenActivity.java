package com.training.crudmakananapp.activity;

import android.os.Bundle;
import android.os.Handler;

import com.training.crudmakananapp.R;
import com.training.crudmakananapp.helper.SessionManager;

public class SplashScreenActivity extends SessionManager {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionManager.checkLogin();
                finish();
            }
        },4000);

    }
}
