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
        this(smallSize, smallSize, smallSize);
    }

    public SimplePhoto(String smallSize, String normalSize){
        this(smallSize,normalSize,normalSize);
    }

    public SimplePhoto(String smallSize, String normalSize, String originSize){
        mSmallSize = smallSize;
        mNormalSize = normalSize;
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

