package com.example.foryaphoto.domain;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Интерфейс к источнику данных для получения фотографий
 */
public interface IDataSource {

    /**
     * Инициализировать источник данных
     */
    void initSource();

    /**
     * Запросить маленькие фотографии
     *
     * @param count     количество запрашиваемых фотографий
     */
    void requestSmallPhotos(int count);

    /**
     * Запросить большие фотографии
     *
     *  @param count     количество запрашиваемых фотографий
     */
    void requestBigPhotos(int count);

    /**
     * Интерфейс обратного вызова при инициализации
     */
    interface IInitSourceCallback{
        /**
         * Вызывается просле завершении инициализации
         */
        void onInit();
    }

    /**
     * Интерфейс обратного вызова для получения маленьких фотографий
     */
    interface ISmallPhotoCallback{
        /**
         * Вызывается при загрузке запрошенных фотографий
         *
         * @param count         количество запрошенных фотографий
         * @param bitmapList    список фотографий
         */
        void onLoadSmall(int count, List<Bitmap> bitmapList);
    }

    /**
     * Интерфейс обратного вызова для получения больших фотографий
     */
    interface IBigPhotoCallback{
        /**
         * Вызывается при загрузке запрошенных фотографий
         *
         * @param count         количество запрошенных фотографий
         * @param bitmapList    список фотографий
         */
        void onLoadBig(int count, List<Bitmap> bitmapList);
    }
}
