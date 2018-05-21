package com.example.foryaphoto;

/**
 * Модель для хранения нескольких вариантов размера фотографии
 *
 * @author Karpachev Alexander
 *         Created on 20.05.2018
 */
public class SimplePhoto {

    private String mSmallSize;
    private String mNormalSize;
    private String mOriginSize;

    public SimplePhoto(String smallSize){
        mSmallSize = smallSize;
        mNormalSize = smallSize;
        mOriginSize = smallSize;
    }

    public SimplePhoto(String smallSize, String normalSize){
        this(smallSize);
        mNormalSize = normalSize;
        mOriginSize = normalSize;
    }

    public SimplePhoto(String smallSize, String normalSize, String originSize){
        this(smallSize, normalSize);
        mOriginSize = originSize;
    }

    /**
     * Задать малый размер фото
     *
     * @param smallSize     размер фото
     */
    public void setSmallSize(String smallSize){
        mSmallSize = smallSize;
    }

    public  void setNormalSize(String normalSize){
        mNormalSize = normalSize;
    }

    public  void setOriginSize(String originSize){
        mOriginSize = originSize;
    }

    public String getSmallSize(){
        return mSmallSize;
    }

    public String getNormalSize(){
        return mNormalSize;
    }

    public String getOriginSize(){
        return mOriginSize;
    }
}

