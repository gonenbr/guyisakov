package com.guy.gonenapp;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MCT6.initHelper();
    }
}
