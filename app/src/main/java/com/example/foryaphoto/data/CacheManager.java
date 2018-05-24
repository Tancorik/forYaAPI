package com.example.foryaphoto.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
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

    public Bitmap getBitmap(String fileURL) {
        Bitmap result;
        String fileName = makeFileName(fileURL);
        try (FileInputStream fileInputStream = mContext.openFileInput(fileName)) {
            result = BitmapFactory.decodeStream(fileInputStream);
        } catch(IOException e) {
            result = null;
            Log.e(LOG_TAG, "Ошибка при попытке открыть или прочитать файл: " + fileName, e);
            e.printStackTrace();
        }
        return result;
    }

    public void cache(String fileURL, Bitmap bitmap) {
        String fileName = makeFileName(fileURL);
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
}
