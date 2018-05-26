package com.example.foryaphoto.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.foryaphoto.data.YandexPhotoLoader.PhotoCallback;

/**
 * @author Aleksandr Karpachev
 *         Created on 26.05.18
 */

public class NewRequestManager {

    public static NewRequestManager getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private static class SingletonHolder {
        private static final NewRequestManager HOLDER_INSTANCE = new NewRequestManager();
    }

    private Map<String, List<PhotoCallback>> mCurrentRequests = new HashMap<>();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Handler mWorkerHandler;

    public void setWorkerHandler(Handler workerHandler) {
        mWorkerHandler = workerHandler;
    }

    public synchronized void startRequest(final PhotoCallback photoCallback) {
        List<PhotoCallback> callbacks = mCurrentRequests.get(photoCallback.getUrl());
        if (callbacks != null) {
            callbacks.add(photoCallback);
            return;
        }

        final List<PhotoCallback> newCallbacks = new ArrayList<>();
        newCallbacks.add(photoCallback);
        mCurrentRequests.put(photoCallback.getUrl(), newCallbacks);
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                loadPhoto(newCallbacks, photoCallback.getUrl());
            }
        });

    }

    private synchronized void onLoad(String url, List<PhotoCallback> callbacks, Bitmap bitmap) {
        mCurrentRequests.remove(url);

        for (PhotoCallback photoCallback: callbacks) {
            photoCallback.setBitmap(bitmap);
            photoCallback.onLoadPhoto();
        }
    }

    @WorkerThread
    private void loadPhoto(final List<PhotoCallback> callbacks, final String url) {
        Bitmap bitmap = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Bitmap result = bitmap;

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onLoad(url, callbacks, result);
            }
        });
    }

}
