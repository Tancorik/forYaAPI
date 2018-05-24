package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.foryaphoto.domain.IDataSource;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IDataSource.IInitSourceCallback,
        IDataSource.ISmallPhotoCallback, IDataSource.IBigPhotoCallback {

//    private YandexPhotoLoader mLoader;
//    private RecyclerView mRecyclerView;
//    private RecyclerAdapter mAdapter;
//
//    private FrameLayout mProgressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mAdapter = new RecyclerAdapter();
//        mRecyclerView = findViewById(R.id.recycler_view);
//        mRecyclerView.setAdapter(mAdapter);
//
//        mProgressBarLayout = findViewById(R.id.progress_bar_layout);
//
//        mLoader = new YandexPhotoLoader(this, this, this);
//        mLoader.initSource();
    }


    @Override
    public void onInit() {
//        mProgressBarLayout.setVisibility(View.VISIBLE);
//        mLoader.requestSmallPhotos(41);
    }

    @Override
    public void onLoadSmall(int count, List<Bitmap> bitmapList) {
//        mAdapter.addPhotos(bitmapList);
//        mAdapter.notifyDataSetChanged();
//        mProgressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoadBig(int count, List<Bitmap> bitmapList) {

    }
}
