package com.example.foryaphoto.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Aleksandr Karpachev
 *         Created on 24.05.18
 */
public class CacheManager {

    private Context mContext;

    private static class SingletonHolder {
        public static final CacheManager HOLDER_INSTANCE = new CacheManager();
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
        UUID fileName = UUID.nameUUIDFromBytes(fileURL.getBytes());
        try(FileInputStream fileInputStream = mContext.openFileInput(fileName.toString())) {
            result = BitmapFactory.decodeStream(fileInputStream);
        } catch(IOException e) {
            return null;
        }
        return result;
    }

    public void cache(String fileURL, Bitmap bitmap) {
        UUID fileName = UUID.nameUUIDFromBytes(fileURL.getBytes());
        try( FileOutputStream fileOutputStream = mContext.openFileOutput(fileName.toString(), mContext.MODE_PRIVATE )) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
