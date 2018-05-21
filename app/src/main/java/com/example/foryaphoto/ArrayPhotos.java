package com.example.foryaphoto;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ArrayPhotos {
    private ArrayList<SimplePhoto> mArray = new ArrayList<>();
    private String mAddress;

    public ArrayPhotos(String address){
        mAddress = address;
        String string = "";
        try {
            string =  getContent();
        }catch (IOException e){
            e.printStackTrace();
        }
    try {
        makeList(string);
    }catch (JSONException e){

    }
    }

    public ArrayList<SimplePhoto> getPhotos(){
        return mArray;
    }

    private String getContent() throws IOException{
        URL url = new URL(mAddress);
        String string = "";
        Log.i("imageTag", "перед подключением");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Accept", "application/json");
        Log.i("imageTag", httpURLConnection.getResponseMessage());
        if (httpURLConnection.getContentLength() != 0) {
            InputStream inputStream = httpURLConnection.getInputStream();
            int transfer;
            StringBuffer stringBuffer = new StringBuffer();
            while ((transfer = inputStream.read()) != -1) {
                stringBuffer.append((char)transfer);
            }
            string = new String(stringBuffer);
            inputStream.close();
        }
        return string;
    }


    //
    //
    private void makeList(String content) throws JSONException{
        String tempString;
        JSONObject jsonObjectTemp = new JSONObject(content);
        tempString = jsonObjectTemp.get("entries").toString();
        JSONArray jsonArray =  new JSONArray(tempString);
        Log.i("myLogs", content);

        for (int count = 0; count< jsonArray.length(); count++){
            jsonObjectTemp = (JSONObject) jsonArray.get(count);
            jsonObjectTemp = (JSONObject) jsonObjectTemp.get("img");

            SimplePhoto simplePhoto = new SimplePhoto("");
            JSONObject jsonObjectSize = (JSONObject) jsonObjectTemp.get("S");
            tempString = jsonObjectSize.get("href").toString();
            simplePhoto.setSmallSize(tempString);

            jsonObjectSize = (JSONObject) jsonObjectTemp.get("XXL");
            tempString = jsonObjectSize.get("href").toString();
            simplePhoto.setNormalSize(tempString);

            try {
                jsonObjectSize = (JSONObject) jsonObjectTemp.get("orig");
                tempString = jsonObjectSize.get("href").toString();

            }catch (NullPointerException e){
                jsonObjectSize = (JSONObject) jsonObjectTemp.get("XXL");
                tempString = jsonObjectSize.get("href").toString();
            }
            simplePhoto.setOriginSize(tempString);
            mArray.add(simplePhoto);
        }
    }

}
