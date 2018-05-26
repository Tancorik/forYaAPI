package com.example.foryaphoto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.foryaphoto.domain.IMyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandr Karpachev
 *         Created on 23.05.18
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<Bitmap> mPhotoList = new ArrayList<>();
    private IMyListener mListener;

    RecyclerAdapter(IMyListener listener) {
        mListener = listener;
    }

    public void addPhotos(List<Bitmap> photoList) {
        mPhotoList.addAll(photoList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items, parent, false);
        return new MyViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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

    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mLeftImageView;
        private final ImageView mRightImageView;
        private final String LEFT_TAG = "left";
        private final String RIGHT_TAG = "right";


        public MyViewHolder(View rowView) {
            super(rowView);
            mLeftImageView = rowView.findViewById(R.id.left_image_view);
            mRightImageView = rowView.findViewById(R.id.right_image_view);
        }

        public void bind(@NonNull final Bitmap left, @Nullable Bitmap right) {
            mLeftImageView.setImageBitmap(left);
            mLeftImageView.setTag(LEFT_TAG);
            mLeftImageView.setOnClickListener(this);
            mRightImageView.setImageBitmap(right);
            mRightImageView.setTag(RIGHT_TAG);
            mRightImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String tag = (String)v.getTag();
            int position = getAdapterPosition() * 2;

            if (tag.equals(RIGHT_TAG)) {
                position++;
            }
            mListener.setCurrentPhoto(mPhotoList.size(), position);
        }
    }
}
