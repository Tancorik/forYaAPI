package com.example.foryaphoto.domain;

import android.graphics.Bitmap;

/**
 * @author Aleksandr Karpachev
 *         Created on 25.05.18
 */

public interface IBigPhotosSource {
    /**
     * Запросить большие фотографии
     *
     *  @param position     количество запрашиваемых фотографий
     */
    void requestBigPhotos(int position);

    /**
     * Интерфейс обратного вызова для получения больших фотографий
     */
    interface IBigPhotoCallback{
        /**
         * Вызывается при загрузке запрошенных фотографий
         *
         * @param position      позиция запрошенной фотографий
         * @param bitmap        большая фотография
         * */
        void onLoadBig(int position, Bitmap bitmap);
    }
}
