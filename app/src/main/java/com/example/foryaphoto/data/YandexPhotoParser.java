package com.example.foryaphoto.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс изъятия информации о фотографиях из нераспарсинной строки
 */
public class YandexPhotoParser {

    private static final String LINKS = "links";
    private static final String NEXT = "next";
    private static final String ENTRIES = "entries";
    private static final String ID = "id";
    private static final String TIME_UPDATED = "updated";
    private static final String TITLE = "title";
    private static final String TIME_PUBLISHED = "published";
    private static final String TIME_EDITED = "edited";
    private static final String TIME_CREATED = "created";
    private static final String IMG = "img";
    private static final String M_SIZE = "M";
    private static final String HREF = "href";
    private static final String XXL_SIZE = "XXL_SIZE";

    private String mNextURL;

    /**
     * Распарсить строку ответа сервера и
     * вернуть информацию о фотографиях в виде списка
     *
     * @param jsonString        нераспарсинная строка
     * @return
     */
    public List<YandexPhotoInfo> parseJSON(String jsonString) {

        YandexPhotoInfo currentPhotoInfo;
        List<YandexPhotoInfo> photoInfoArray = new ArrayList<>();

        String mId;
        String mTitle;
        String mPublishedTime;
        String mUpdatedTime;
        String mEditedTime;
        String mCreatedTime;
        String mSmallSizeURL;
        String mBigSizeURL;

        try {
            JSONObject jsonObjectTemp = new JSONObject(jsonString);
            String tempString = jsonObjectTemp.getString(LINKS);
            JSONObject jsonlinks = new JSONObject(tempString);
            mNextURL = jsonlinks.getString(NEXT);

            tempString = jsonObjectTemp.getString(ENTRIES);
            JSONArray jsonArray = new JSONArray(tempString);

            for (int i = 0; i<jsonArray.length(); i++) {
                jsonObjectTemp = (JSONObject)jsonArray.get(i);
                mId =jsonObjectTemp.getString(ID);
                mUpdatedTime = jsonObjectTemp.getString(TIME_UPDATED);
                mTitle = jsonObjectTemp.getString(TITLE);
                mPublishedTime = jsonObjectTemp.getString(TIME_PUBLISHED);
                mEditedTime = jsonObjectTemp.getString(TIME_EDITED);
                mCreatedTime = jsonObjectTemp.getString(TIME_CREATED);

                JSONObject jsonObjectSize = jsonObjectTemp.getJSONObject(IMG);
                jsonObjectSize = jsonObjectSize.getJSONObject(M_SIZE);
                mSmallSizeURL = jsonObjectSize.getString(HREF);

                jsonObjectSize = jsonObjectTemp.getJSONObject(IMG);
                jsonObjectSize = jsonObjectSize.getJSONObject(XXL_SIZE);
                mBigSizeURL = jsonObjectSize.getString(HREF);

                currentPhotoInfo = new YandexPhotoInfo.Builder()
                        .setId(mId)
                        .setUpdatedTime(mUpdatedTime)
                        .setTitle(mTitle)
                        .setPublishedTime(mPublishedTime)
                        .setEditedTime(mEditedTime)
                        .setCreatedTime(mCreatedTime)
                        .setSmallSizeURL(mSmallSizeURL)
                        .setBigSizeURL(mBigSizeURL)
                        .build();
                photoInfoArray.add(currentPhotoInfo);
            }
        }
        catch(JSONException e) {
            throw new RuntimeException("ошибка при парсинге", e);
        }
        return photoInfoArray;
    }

    /**
     * Венуть строку для следующего запроса
     *
     * @return
     */
    public String getNextURL(){
        return mNextURL;
    }
}
