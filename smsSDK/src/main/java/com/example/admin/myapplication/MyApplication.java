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
        SMSSDK.initSDK(this, "appkey", "appserect");
    }
}
