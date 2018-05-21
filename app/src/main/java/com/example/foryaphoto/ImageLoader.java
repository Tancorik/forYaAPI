package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader {
    private static final String TAG = "imageTag";
    public static void setImage(final String url, final ImageView imageView){
        Handler.Callback handlerCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                imageView.setImageBitmap((Bitmap)msg.obj);
                return false;
            }
        };

        final Handler handler = new Handler(handlerCallback);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap image = downloadBitmap(url);
                if (image != null){
                    final Message msg = handler.obtainMessage(1,image);
                    handler.sendMessage(msg);
                }
            }
        });
        Log.i(TAG,"запускаем новый поток");
        thread.start();
    }

    private static Bitmap downloadBitmap(String urlString){
        Bitmap bitmap = null;
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        try{InputStream inputStream;
            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.i(TAG,"загрузили");
        }catch (MalformedURLException e){
            Log.i(TAG,"что-то не то с URL");
        }catch (IOException ie){
            Log.i(TAG,"что-то не то с IO");
        }
        return bitmap;
    }
}
