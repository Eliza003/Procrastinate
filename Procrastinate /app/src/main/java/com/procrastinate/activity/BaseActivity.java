package com.procrastinate.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init event bus
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}