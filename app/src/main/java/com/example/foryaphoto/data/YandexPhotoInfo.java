package com.example.foryaphoto.data;

/**
 * Класс хранения информации об одной фотографии
 */
public class YandexPhotoInfo {

    public final String mId;
    public final String mTitle;
    public final String mPublishedTime;
    public final String mEditedTime;
    public final String mUpdatedTime;
    public final String mCreatedTime;
    public final String mSmallSizeURL;
    public final String mBigSizeURL;

    /**
     * Конструктор
     *
     * @param id                id фотографии
     * @param title             название фотографии
     * @param publishedTime     дата публикации фотографии
     * @param mEditedTime       дата изменения фотографии
     * @param mUpdatedTime      дата обновления фотографии
     * @param mCreatedTime      дата создания фотографии
     * @param mSmallSizeURL     URL-адрес фотографии малого размера
     * @param mBigSizeURL       URL-адрес фотографии большого размера
     */
    private YandexPhotoInfo(String id, String title, String publishedTime,
                            String mEditedTime, String mUpdatedTime, String mCreatedTime,
                            String mSmallSizeURL, String mBigSizeURL) {
        this.mId = id;
        this.mTitle = title;
        this.mPublishedTime = publishedTime;
        this.mEditedTime = mEditedTime;
        this.mUpdatedTime = mUpdatedTime;
        this.mCreatedTime = mCreatedTime;
        this.mSmallSizeURL = mSmallSizeURL;
        this.mBigSizeURL = mBigSizeURL;
    }

    /**
     * Класс стоитель
     */
    public static class Builder {
        private String mId;
        private String mTitle;
        private String mPublishedTime;
        private String mEditedTime;
        private String mUpdatedTime;
        private String mCreatedTime;
        private String mSmallSizeURL;
        private String mBigSizeURL;

        public Builder setId(String mId) {
            this.mId = mId;
            return this;
        }

        public Builder setTitle(String mTitle) {
            this.mTitle = mTitle;
            return this;
        }

        public Builder setPublishedTime(String mPublishedTime) {
            this.mPublishedTime = mPublishedTime;
            return this;
        }

        public Builder setEditedTime(String mEditedTime) {
            this.mEditedTime = mEditedTime;
            return this;
        }

        public Builder setUpdatedTime(String mUpdatedTime) {
            this.mUpdatedTime = mUpdatedTime;
            return this;
        }

        public Builder setCreatedTime(String mCreatedTime) {
            this.mCreatedTime = mCreatedTime;
            return this;
        }

        public Builder setSmallSizeURL(String mSmallSizeURL) {
            this.mSmallSizeURL = mSmallSizeURL;
            return this;
        }

        public Builder setBigSizeURL(String mBigSizeURL) {
            this.mBigSizeURL = mBigSizeURL;
            return this;
        }

        public YandexPhotoInfo build() {
            return new YandexPhotoInfo(mId, mTitle, mPublishedTime,
                    mEditedTime, mUpdatedTime, mCreatedTime,
                    mSmallSizeURL, mBigSizeURL);
        }
    }
}
