package com.chanyoung.market;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.CustomViewHolder> {

    private Context mContext;
    private List<multipleImage> list;

    public ImageSliderAdapter(Context mContext, List<multipleImage> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @NonNull
    @Override
    public ImageSliderAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider, parent, false);

        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderAdapter.CustomViewHolder holder, int position) {

        String uri = list.get(position).getPath();
            holder.bindImage(uri);

    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageSlider);

        }

        public void bindImage(String uri) {
            Glide.with(mContext)
                    .load(uri)
                    .into(imageView);
        }
    }
}
