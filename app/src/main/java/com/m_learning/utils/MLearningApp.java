package com.m_learning.utils;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;


public class MLearningApp extends MultiDexApplication implements LifecycleObserver {
    private static MLearningApp instance;
    private static Boolean isChatActivityOpen = false;

    public static MLearningApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        MobileAds.initialize(this, initializationStatus -> {

        });

    }



    public static Boolean isChatActivityOpen() {
        return isChatActivityOpen;
    }

    public static void setChatActivityOpen(Boolean isOpen) {
        isChatActivityOpen = isOpen;
    }


}
