package com.example.foryaphoto.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Кэш-менеджер, кэширующий загруженные фотографии на диске.
 * Работает в рабочем потоке.
 *
 * @author Aleksandr Karpachev
 *         Created on 24.05.18
 */
public class CacheManager {

    private static final String LOG_TAG = "CacheManager";

    private Context mContext;

    private static class SingletonHolder {
        private static final CacheManager HOLDER_INSTANCE = new CacheManager();
    }

    public static CacheManager getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public void setContext(Context context) {
        if (mContext != null) {
            throw new IllegalStateException("контекст уже задан");
        }
        mContext = context;
        for (String fileName : mContext.fileList()) {
            mContext.deleteFile(fileName);
        }
    }

    public void getBitmap(final String fileURL, Handler workerHandler, final ICallback callback) {
        Log.e(LOG_TAG, "Рабочий поток: " + workerHandler.getLooper().getThread().getName());
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                final Bitmap finalResult = getBitmap(fileURL);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onGetBitmap(finalResult);
                    }
                });
            }
        });
    }

    public void cache(final String fileURL, final Bitmap bitmap, Handler workerHandler) {
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                cache(fileURL, bitmap);
            }
        });
    }

    @WorkerThread
    public Bitmap getBitmap(String url) {
        Bitmap result;
        String fileName = makeFileName(url);
        try (FileInputStream fileInputStream = mContext.openFileInput(fileName)) {
            result = BitmapFactory.decodeStream(fileInputStream);
        } catch(IOException e) {
            result = null;
            Log.e(LOG_TAG, "Ошибка при попытке открыть или прочитать файл: " + fileName, e);
            e.printStackTrace();
        }
        return result;
    }

    @WorkerThread
    public void cache(String url, Bitmap bitmap) {
        String fileName = makeFileName(url);
        try (
                FileOutputStream fileOutputStream = mContext.openFileOutput(
                        fileName, Context.MODE_PRIVATE ))
        {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Ошибка при записи приватного файла: " + fileName, e);
        }
    }

    private String makeFileName(String fileUrl) {
        return UUID.nameUUIDFromBytes(fileUrl.getBytes()).toString();
    }

    public interface ICallback {
        void onGetBitmap(Bitmap bitmap);
    }
}
