package com.example.foryaphoto;

import android.content.Intent;
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
import com.example.foryaphoto.domain.IMyListener;
import com.example.foryaphoto.domain.ISmallPhotosSource;

import java.util.List;

/**
 * @author Aleksandr Karpachev
 *         Created on 24.05.18
 */

public class MainFragment extends Fragment implements ISmallPhotosSource.IInitSourceCallback,
        ISmallPhotosSource.ISmallPhotoCallback, IMyListener {

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

        mAdapter = new RecyclerAdapter(this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);

        mLoader = YandexPhotoLoader.getInstance();
        mLoader.setInitCallback(this);
        mLoader.setSmallPhotoCallback(this);
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

    /**
     * Получить позицию фотки которую нужно загрузить в новой Активити
     *
     * @param position      позиция фотки по которой клинул пользователь
     */
    @Override
    public void setCurrentPhoto(int count, int position) {
        Intent intent = PagerActivity.newIntent(getContext(), position);
        startActivity(intent);
    }
}