package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.foryaphoto.data.YandexPhotoLoader;
import com.example.foryaphoto.domain.IBigPhotosSource;

/**
 * @author Alexsandr Karpachev
 *         Created on 24.05.18
 */

public class PagerFragment extends Fragment  implements IBigPhotosSource.IBigPhotoCallback {

    public static final String TAG = "PagerFragment";
    private static final String ARGUMENT_PAGE_NUMBER = "page_number";

    private int mCurrentIndex;
    private IBigPhotosSource mPhotosSource;
    private ImageView mImageView;
    private Button mPrevButton;
    private Button mNextButton;

    /**
     * Создать новый экземпляр фрагмента с атрибутом - номером страницы
     *
     * @param index
     * @return
     */
    public static PagerFragment newInstance(int index) {
        PagerFragment pagerFragment = new PagerFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, index);
        pagerFragment.setArguments(arguments);
        return pagerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentIndex = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        mPhotosSource = YandexPhotoLoader.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_pager, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = view.findViewById(R.id.big_image_view);
        mPrevButton = view.findViewById(R.id.left_button_view);
        mNextButton = view.findViewById(R.id.right_button_view);
        YandexPhotoLoader.getInstance().setBigPhotoCallback(this);
        mPhotosSource.requestBigPhotos(mCurrentIndex);

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableButtons(false);
                mPhotosSource.requestBigPhotos(mCurrentIndex - 1);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableButtons(false);
                mPhotosSource.requestBigPhotos(mCurrentIndex + 1);
            }
        });
    }

    @Override
    public void onLoadBig(int position, Bitmap bitmap) {
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
            mCurrentIndex = position;
            getArguments().putInt(ARGUMENT_PAGE_NUMBER, mCurrentIndex);
        }
        enableButtons(true);
    }

    private void enableButtons(boolean enabled) {
        mPrevButton.setEnabled(enabled);
        mNextButton.setEnabled(enabled);
    }
}
