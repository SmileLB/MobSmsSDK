package com.example.admin.myapplication;

import android.app.Application;

import cn.smssdk.SMSSDK;

/**
 * Description:
 * Created by LiBing
 * Data:2017/5/24 10:42
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SMSSDK.initSDK(this, "1e2193bc06926", "ec8a27d8a4b29cff2619a06bb7dd1460");
    }
}
