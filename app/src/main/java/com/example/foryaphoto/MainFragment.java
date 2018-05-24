package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.foryaphoto.data.YandexPhotoLoader;
import com.example.foryaphoto.domain.IDataSource;

import java.util.List;

/**
 * @author Aleksandr Karpachev
 *         Created on 24.05.18
 */

public class MainFragment extends Fragment implements IDataSource.IInitSourceCallback,
        IDataSource.ISmallPhotoCallback, IDataSource.IBigPhotoCallback{


    private YandexPhotoLoader mLoader;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

    private FrameLayout mProgressBarLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new RecyclerAdapter();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);

        mLoader = new YandexPhotoLoader(this, this, this);
        mLoader.initSource();

        return view;
    }

    @Override
    public void onInit() {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        mLoader.requestSmallPhotos(41);
    }

    @Override
    public void onLoadSmall(int count, List<Bitmap> bitmapList) {
        mAdapter.addPhotos(bitmapList);
        mAdapter.notifyDataSetChanged();
        mProgressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoadBig(int count, List<Bitmap> bitmapList) {

    }
}
