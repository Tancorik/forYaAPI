package com.example.foryaphoto;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ArrayList<SimplePhoto> mPhotos = new ArrayList<>();
    int mCount= -1;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        ImageLoader.setImage("http://grand-screen.com/blog/wp-content/uploads/2016/01/d6.jpg", imageView);

        fillArray();

        Button button = findViewById(R.id.button);
        button.setText(String.valueOf(mPhotos.size()));
        button.setOnClickListener(listener);

    }

    private void fillArray(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mPhotos = new ArrayPhotos("http://api-fotki.yandex.ru/api/top/?limit=20").getPhotos();
            }
        });
        thread.start();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCount++;
            if (mCount == mPhotos.size())
                mCount = 0;
            ImageLoader.setImage(mPhotos.get(mCount).getNormalSize(),imageView);
            Button button = findViewById(v.getId());
            button.setText(String.valueOf(mPhotos.size()));
        }
    };
}
