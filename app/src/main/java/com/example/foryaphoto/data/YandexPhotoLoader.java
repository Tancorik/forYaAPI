package com.example.foryaphoto.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.util.Log;

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
    private static final int PRELOAD_COUNT = 20;
    private static final int STEP_PREV = -1;
    private static final int STEP_NEXT = 1;
    public static final String LOG_TAG = "YandexPhotoLoaderTag";

    public static final int REQUEST_THREAD_ID = 1;
    public static final int CACHE_THREAD_ID = 2;

    private RequestManager mRequestManager;
    private CacheManager mCacheManager;
    private String mNextDataURL;
    private Handler mRequestHandler;
    private Handler mCacheHandler;
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
        new LoaderThread(this, REQUEST_THREAD_ID).start();
        new LoaderThread(this, CACHE_THREAD_ID).start();
        mRequestManager = RequestManager.getInstance();
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
        if (isAllHandlersReady())
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
        final String url = mInfoList.get(index).mBigSizeURL;

        mCacheManager.getBitmap(url, mCacheHandler, new CacheManager.ICallback() {
            @Override
            public void onGetBitmap(final Bitmap bitmap) {
                if (bitmap == null) {
                    mRequestManager.startRequest(new PhotoCallback(url) {
                        @Override
                        public void onLoadPhoto() {
                            if (mBigPhotoCallback != null)
                                mBigPhotoCallback.onLoadBig(index, getBitmap());
                            mCacheManager.cache(getUrl(), getBitmap(), mCacheHandler);
                        }
                    });
                    Log.e(LOG_TAG, "В кэше отсутсвует: " + url);
                }
                else {
                    Log.e(LOG_TAG, "Получено из кэша: " + url);
                    if (mBigPhotoCallback != null)
                        mBigPhotoCallback.onLoadBig(index, bitmap);
                }
                preloadPhotos(index, PRELOAD_COUNT, STEP_NEXT);
                preloadPhotos(index, PRELOAD_COUNT, STEP_PREV);
            }
        });
    }

    /**
     * Получить хендлер из отдльного потока
     *
     * @param handler
     */
    @Override
    public void onHandlerInit(Handler handler, int id) {
        switch (id) {
            case REQUEST_THREAD_ID:
                mRequestHandler = handler;
                mRequestManager.setWorkerHandler(mRequestHandler);
                break;
            case CACHE_THREAD_ID:
                mCacheHandler = handler;
                break;
        }
        if (isAllHandlersReady())
            mInitCallback.onInit();
    }

    private boolean isAllHandlersReady() {
        return mRequestHandler != null && mCacheHandler != null;
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
    private void preloadPhotos(int baseIndex, int count, int step) {
        while (count > 0) {
            baseIndex += step;
            if (baseIndex < 0 || baseIndex >= mInfoList.size())
                return;
            final String url = mInfoList.get(baseIndex).mBigSizeURL;
            mCacheManager.getBitmap(url, mCacheHandler, new CacheManager.ICallback() {
                @Override
                public void onGetBitmap(Bitmap bitmap) {
                    if (bitmap == null) {
                        mRequestManager.startRequest(new PhotoCallback(url) {
                            @Override
                            public void onLoadPhoto() {
                                mCacheManager.cache(getUrl(), getBitmap(), mCacheHandler);
                            }
                        });
                    }
                }
            });
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
        mRequestHandler.post(new Runnable() {
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
        mRequestHandler.post(new Runnable() {
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

    public static abstract class PhotoCallback {

        private final String mUrl;
        private Bitmap mBitmap;

        public PhotoCallback(String url) {
            mUrl = url;
        }

        public String getUrl() {
            return mUrl;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        public abstract void onLoadPhoto();
    }

    private interface IInfoLoadCallback {
        void onLoadInfo(int count);
    }
}
