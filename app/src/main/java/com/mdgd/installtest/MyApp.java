package com.mdgd.installtest;

import android.app.Application;

/**
 * Created by Owner
 * on 01/04/2018.
 */
public class MyApp extends Application {

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
