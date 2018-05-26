package com.example.foryaphoto.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;

import com.example.foryaphoto.domain.IBigPhotosSource;
import com.example.foryaphoto.domain.ISmallPhotosSource;

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
public class YandexPhotoLoader implements IBigPhotosSource, ISmallPhotosSource,
        LoaderThread.IHandlerInitCallback {

    private static final String  DATA_URL = "http://api-fotki.yandex.ru/api/top/";
    private static final int PRELOAD_COUNT = 5;
    private static final int STEP_PREV = -1;
    private static final int STEP_NEXT = 1;

    private CacheManager mCacheManager;
    private String mNextDataURL;
    private Handler mWorkerHandler;
    private Handler mMainHandler;
    private IInitSourceCallback mInitCallback;
    private ISmallPhotoCallback mSmallPhotoCallback;
    private IBigPhotoCallback mBigPhotoCallback;
    private YandexPhotoParser mParser;
    private List<YandexPhotoInfo> mInfoList;

    private static class SingletonHolder {
        private static final YandexPhotoLoader HOLDER_INSTANCE = new YandexPhotoLoader();
    }

    public static YandexPhotoLoader getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }


    YandexPhotoLoader() {
        mCacheManager = CacheManager.getInstance();
        mParser = new YandexPhotoParser();
        mMainHandler = new Handler(Looper.getMainLooper());
        new LoaderThread(this).start();
    }

    public void setInitCallback(IInitSourceCallback initCallback) {
        mInitCallback = initCallback;
    }

    public void setSmallPhotoCallback(ISmallPhotoCallback smallPhotoCallback) {
        mSmallPhotoCallback = smallPhotoCallback;
    }

    public void setBigPhotoCallback(IBigPhotoCallback bigPhotoCallback) {
        mBigPhotoCallback = bigPhotoCallback;
    }

    /**
     * Инициализирует источник данных
     */
    @Override
    public void initSource() {
        mNextDataURL = null;
        if (mWorkerHandler != null)
            mInitCallback.onInit();
    }

    /**
     * Получить маленькие фотографии из данных
     *
     * @param count     количество запрашиваемых фотографий
     */
    @Override
    public void requestSmallPhotos(final int count) {
        IInfoLoadCallback onInfoLoad = new IInfoLoadCallback() {
            @Override
            public void onLoadInfo(int count) {
                loadSmallPhotos(count);
            }
        };
        loadInfoList(count, onInfoLoad);
    }

    /**
     * Получить большие фотографии из данных
     *
     * @param number     номер запрашиваемой фотографий
     */
    @Override
    public void requestBigPhotos(int number) {
        if (mBigPhotoCallback == null) {
            return;
        }

        if (number < 0) {
            number = 0;
            mBigPhotoCallback.onLoadBig(number, null);
            return;
        }
        else if (number >= mInfoList.size()) {
            number = mInfoList.size() - 1;
            mBigPhotoCallback.onLoadBig(number, null);
            return;
        }

        final int index = number;

        mCacheManager.getBitmap(mInfoList.get(index).mBigSizeURL, mWorkerHandler, new CacheManager.ICallback() {
            @Override
            public void onGetBitmap(Bitmap bitmap) {
                if (bitmap == null) {
                    mWorkerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = loadBigPhotos(mInfoList.get(index));
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (bitmap != null)
                                        mCacheManager.cache(mInfoList.get(index).mBigSizeURL, bitmap, mWorkerHandler);
                                    if (mBigPhotoCallback != null)
                                        mBigPhotoCallback.onLoadBig(index, bitmap);
                                }
                            });
                        }
                    });
                }
                else {
                    if (mBigPhotoCallback != null)
                        mBigPhotoCallback.onLoadBig(index, bitmap);
                }

                mWorkerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        preloadPhotos(index, PRELOAD_COUNT, STEP_NEXT);
                        preloadPhotos(index, PRELOAD_COUNT, STEP_PREV);
                    }
                });
            }
        });
    }

    /**
     * Получить хендлер из отдльного потока
     *
     * @param handler
     */
    @Override
    public void onHandlerInit(Handler handler) {
        mWorkerHandler = handler;
        mInitCallback.onInit();
    }

    /**
     * Получить поток для чтения информации о фотографиях
     *
     * @param count     количество запрашиваемых фотографий
     * @return          поток с данными о фотографиях
     */
    @WorkerThread
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
     *
     * @param inputStream   поток для чтения информации
     * @return
     */
    @WorkerThread
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

    /** Предзагрузить в кэш следующие/предыдущие фотографии */
    @WorkerThread
    private void preloadPhotos(int baseIndex, int count, int step) {
        while (count > 0) {
            baseIndex += step;
            if (baseIndex < 0 || baseIndex >= mInfoList.size())
                return;
            Bitmap bitmap = mCacheManager.getBitmap(mInfoList.get(baseIndex).mBigSizeURL);
            if (bitmap == null) {
                bitmap = loadBigPhotos(mInfoList.get(baseIndex));
                if (bitmap != null) {
                    mCacheManager.cache(mInfoList.get(baseIndex).mBigSizeURL, bitmap);
                }
            }
            count--;
        }
    }

    /**
     * Задать URL-адрес для запроса информации
     *
     * @param count     количество запрашиваемых фотографий
     * @return
     */
    @WorkerThread
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

    private void loadInfoList(final int count, final IInfoLoadCallback loadCallback) {
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = requestPhotoInfo(count);
                String jsonString = readString(inputStream);
                final YandexPhotoParser parser = new YandexPhotoParser();
                final List<YandexPhotoInfo> infoList = parser.parseJSON(jsonString);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onLoadInfoList(infoList, parser.getNextURL());
                        loadCallback.onLoadInfo(count);
                    }
                });
            }
        });
    }

    private void onLoadInfoList(List<YandexPhotoInfo> infoList, String nextDataURL) {
        mInfoList = infoList;
        mNextDataURL = nextDataURL;
    }

    private void loadSmallPhotos(final int count) {
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                final List<Bitmap> photos = new ArrayList<>(mInfoList.size());
                Bitmap bitmap;
                try {
                    for (YandexPhotoInfo photoInfo: mInfoList) {
                        bitmap = mCacheManager.getBitmap(photoInfo.mSmallSizeURL);
                        if (bitmap == null) {
                            HttpURLConnection connection = ((HttpURLConnection) new URL(photoInfo.mSmallSizeURL).openConnection());
                            InputStream inputStream = connection.getInputStream();
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            mCacheManager.cache(photoInfo.mSmallSizeURL, bitmap);
                        }
                        photos.add(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // вызываем колбэк в основном потоке
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSmallPhotoCallback != null)
                            mSmallPhotoCallback.onLoadSmall(count, photos);
                    }
                });
            }
        });
    }

    @WorkerThread
    private Bitmap loadBigPhotos(YandexPhotoInfo photoInfo) {
        Bitmap bitmap = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(photoInfo.mBigSizeURL).openConnection();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private interface IInfoLoadCallback {
        void onLoadInfo(int count);
    }
}
