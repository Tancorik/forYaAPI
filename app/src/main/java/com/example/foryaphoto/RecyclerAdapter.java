package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandr Karpachev
 *         Created on 23.05.18
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AdapterHolder> {

    private List<Bitmap> mPhotoList = new ArrayList<>();

    RecyclerAdapter() {
    }

    public void addPhotos(List<Bitmap> photoList) {
        mPhotoList.addAll(photoList);
    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items, parent, false);
        return new AdapterHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
        int leftPhotoListIndex = position * 2;
        int rightPhotoListIndex = leftPhotoListIndex + 1;

        holder.bind(mPhotoList.get(leftPhotoListIndex),
                rightPhotoListIndex < mPhotoList.size()
                        ? mPhotoList.get(rightPhotoListIndex)
                        : null);
    }

    @Override
    public int getItemCount() {
        return (mPhotoList.size() + 1) / 2;
    }

    protected class AdapterHolder extends RecyclerView.ViewHolder{

        private final ImageView mLeftImageView;
        private final ImageView mRightImageView;

        public AdapterHolder(View rowView) {
            super(rowView);
            mLeftImageView = rowView.findViewById(R.id.left_image_view);
            mRightImageView = rowView.findViewById(R.id.right_image_view);
        }

        public void bind(@NonNull Bitmap left, @Nullable Bitmap right) {
            mLeftImageView.setImageBitmap(left);
            mRightImageView.setImageBitmap(right);
        }
    }
}
