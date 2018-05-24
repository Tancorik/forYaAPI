package com.example.foryaphoto;

import android.app.Application;

import com.example.foryaphoto.data.CacheManager;

/**
 * @author Aleksandr Karpachev
 *         Created on 24.05.18
 */

public class MyApplication extends Application {

        @Override
    public void onCreate() {
        super.onCreate();
        CacheManager.getInstance().setContext(getApplicationContext());
    }
}
