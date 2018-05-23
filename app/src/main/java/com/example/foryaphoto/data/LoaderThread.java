package com.example.foryaphoto.data;

import android.os.Handler;
import android.os.Looper;

/**
 * Класс отдельного потока для YandexPhotoLoader
 */
public class LoaderThread extends Thread{

    private IHandlerInitCallback mCallback;
    private Handler mHandler;
    LoaderThread(IHandlerInitCallback callback) {
        mCallback = callback;
    }

    public void run() {
        Looper.prepare();

        mHandler = new Handler();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mCallback.onHandlerInit(mHandler);
            }
        });

        Looper.loop();
    }

    interface IHandlerInitCallback {
        void onHandlerInit(Handler handler);
    }
}
