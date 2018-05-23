package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.foryaphoto.data.YandexPhotoLoader;
import com.example.foryaphoto.domain.IDataSource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IDataSource.IInitSourceCallback,
        IDataSource.ISmallPhotoCallback, IDataSource.IBigPhotoCallback {

    ImageView imageView;
    ArrayList<SimplePhoto> mPhotos = new ArrayList<>();
    int mCount= -1;

    private YandexPhotoLoader mLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoader = new YandexPhotoLoader(this, this, this);
        mLoader.initSource();
//        imageView = findViewById(R.id.imageView);
//        ImageLoader.setImage("http://grand-screen.com/blog/wp-content/uploads/2016/01/d6.jpg", imageView);
//
//        fillArray();
//
//        Button button = findViewById(R.id.button);
//        button.setText(String.valueOf(mPhotos.size()));
//        button.setOnClickListener(listener);

    }

    @Override
    public void onInit() {
        mLoader.requestSmallPhotos(5);
    }

    @Override
    public void onLoadSmall(int count, List<Bitmap> bitmapList) {

    }

    @Override
    public void onLoadBig(int count, List<Bitmap> bitmapList) {

    }

    //    private void fillArray(){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mPhotos = new ArrayPhotos("http://api-fotki.yandex.ru/api/top/?limit=20").getPhotos();
//            }
//        });
//        thread.start();
//    }
//
//    View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            mCount++;
//            if (mCount == mPhotos.size())
//                mCount = 0;
//            ImageLoader.setImage(mPhotos.get(mCount).getNormalSize(),imageView);
//            Button button = findViewById(v.getId());
//            button.setText(String.valueOf(mPhotos.size()));
//        }
//    };
}
