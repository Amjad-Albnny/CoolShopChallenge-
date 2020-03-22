package com.amjadalbnny.coolshopchallenge;

import android.app.Application;

import com.amjadalbnny.coolshopchallenge.managers.SharedPreferencesManager;

public class App extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        SharedPreferencesManager.getInstance().init(this);
    }

}
