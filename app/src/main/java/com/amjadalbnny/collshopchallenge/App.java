package com.amjadalbnny.collshopchallenge;

import android.app.Application;

import com.amjadalbnny.collshopchallenge.managers.SharedPreferencesManager;

public class App extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        SharedPreferencesManager.getInstance().init(this);
    }

}
