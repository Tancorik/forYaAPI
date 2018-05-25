package com.example.foryaphoto.domain;

import android.graphics.Bitmap;

import java.util.List;

/**
 * @author ALeksandr Karpachev
 *         Created on 25.05.18
 */

public interface ISmallPhotosSource {
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
    interface ISmallPhotoCallback {
        void onLoadSmall(int count, List<Bitmap> bitmapList);
    }

}
