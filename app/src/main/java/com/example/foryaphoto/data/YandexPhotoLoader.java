package com.example.foryaphoto.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.example.foryaphoto.domain.IDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс предоставления фотографий в виде Bitmap
 */
public class YandexPhotoLoader implements IDataSource, LoaderThread.IHandlerInitCallback {

    private static final String  DATA_URL = "http://api-fotki.yandex.ru/api/recent/";

    private String mNextDataURL;
    private Handler mHandler;
    private IInitSourceCallback mInitCallback;
    private ISmallPhotoCallback mSmallPhotoCallback;
    private IBigPhotoCallback mBigPhotoCallback;
    private YandexPhotoParser mParser;

    /**
     * Конструктор
     *
     * @param initCallback
     * @param smallPhotoCallback
     * @param bigPhotoCallback
     */
    public YandexPhotoLoader(IInitSourceCallback initCallback, ISmallPhotoCallback smallPhotoCallback,
                      IBigPhotoCallback bigPhotoCallback) {
        mInitCallback = initCallback;
        mSmallPhotoCallback = smallPhotoCallback;
        mBigPhotoCallback = bigPhotoCallback;
        mParser = new YandexPhotoParser();
        new LoaderThread(this).start();
    }

    /**
     * Инициализирует источник данных
     */
    @Override
    public void initSource() {
        mNextDataURL = null;
        if (mHandler != null)
            mInitCallback.onInit();
    }

    /**
     * Получить маленькие фотографии из данных
     *
     * @param count     количество запрашиваемых фотографий
     */
    @Override
    public void requestSmallPhotos(final int count) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = requestPhotoInfo(count);
                String jsonString = readString(inputStream);
                YandexPhotoParser parser = new YandexPhotoParser();
                List<YandexPhotoInfo> infoList = parser.parseJSON(jsonString);
                final List<Bitmap> photos = loadSmallPhotos(infoList);
                final String nextUrl = parser.getNextURL();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mNextDataURL = nextUrl;
                        mSmallPhotoCallback.onLoadSmall(count, photos);
                    }
                });
            }
        });
    }

    /**
     * Получить большие фотографии из данных
     *
     * @param count     количество запрашиваемых фотографий
     */
    @Override
    public void requestBigPhotos(int count) {

    }

    /**
     * Получить хендлер из отдльного потока
     *
     * @param handler
     */
    @Override
    public void onHandlerInit(Handler handler) {
        mHandler = handler;
        mInitCallback.onInit();
    }

    /**
     * Получить поток для чтения информации о фотографиях
     *
     * @param count     количество запрашиваемых фотографий
     * @return          поток с данными о фотографиях
     */
    protected InputStream requestPhotoInfo(int count) {
        String url = makeURL(count);
        InputStream inputStream;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
            httpURLConnection.setRequestProperty("Accept", "application/json");
            inputStream = httpURLConnection.getInputStream();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("неправильный URL: " + url);
        }
        catch (IOException e) {
            throw new RuntimeException("ошибка ввода вывода", e);
        }
        return inputStream;
    }

    /**
     * Получить информацию о фотографиях в виде строки
     * @param inputStream   поток для чтения информации
     * @return
     */
    protected String readString(InputStream inputStream) {
        int symbol;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((symbol = inputStream.read()) != -1) {
                stringBuilder.append((char) symbol);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("ошибка ввода вывода при чтении", e);
        }
        return stringBuilder.toString();
    }

    /**
     * Задать URL-адрес для запроса информации
     * @param count     количество запрашиваемых фотографий
     * @return
     */
    protected String makeURL(int count) {
        String url;
        if (mNextDataURL == null) {
            url = DATA_URL + "?limit=" + count;
        }
        else {
            url = mNextDataURL;
        }
        return url;
    }

    private List<Bitmap> loadSmallPhotos(List<YandexPhotoInfo> photoInfoList) {
        List<Bitmap> photos = new ArrayList<>(photoInfoList.size());
        try {
            for (YandexPhotoInfo photoInfo: photoInfoList) {
                HttpURLConnection connection = ((HttpURLConnection) new URL(photoInfo.mSmallSizeURL).openConnection());
                InputStream inputStream = connection.getInputStream();
                photos.add(BitmapFactory.decodeStream(inputStream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photos;
    }

}
