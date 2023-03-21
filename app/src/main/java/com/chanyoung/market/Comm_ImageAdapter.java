package com.chanyoung.market;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Comm_ImageAdapter extends RecyclerView.Adapter<Comm_ImageAdapter.CustomViewHolder> {

    private ArrayList<Uri> list;
    private Context context;
    private TextView image_num;

    public Comm_ImageAdapter(Context context, ArrayList<Uri> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Comm_ImageAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_sale_image_rv, parent, false);
        CustomViewHolder holder=  new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Comm_ImageAdapter.CustomViewHolder holder, int position) {

        int i = holder.getAdapterPosition();
        image_num = ((upload_comm_post)context).findViewById(R.id.upload_comm_image_num);

        Uri uri = list.get(position);

        Glide.with(context)
                .load(uri)
                .into(holder.imageView);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, list.size());
                image_num.setText(list.size() + " / 10");
            }
        });

    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        private ImageButton delete;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageView = (ImageView) itemView.findViewById(R.id.upload_sale_image_rv);
            this.delete = (ImageButton) itemView.findViewById(R.id.delete_image_item);
        }
    }
}
