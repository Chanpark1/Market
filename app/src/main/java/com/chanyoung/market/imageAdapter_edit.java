package com.chanyoung.market;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
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
import java.util.List;

public class imageAdapter_edit extends RecyclerView.Adapter<imageAdapter_edit.CustomViewHolder> {

    private List<Uri> list;
    private List<Uri> deleted_list;
    private List<Uri> added_list;
    private Context mContext;
    private TextView image_num;

    public imageAdapter_edit(Context mContext, List<Uri> list, List<Uri> deleted_list, List<Uri> added_list) {
        this.mContext = mContext;
        this.list = list;
        this.deleted_list = deleted_list;
        this.added_list = added_list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_sale_image_rv,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        int i = holder.getAdapterPosition();
        image_num = ((edit_sale_post)mContext).findViewById(R.id.edit_image_num);

        Uri uri = list.get(position);

        Glide.with(mContext)
                .load(uri)
                .into(holder.imageView);


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String jj = list.get(i).toString();
                Log.d("원본 어레이", jj);
                Log.d("deleted 어레이", deleted_list.get(i).toString());

                for(int i = 0; i < deleted_list.size(); i++) {
                    if(deleted_list.get(i).toString().equals(jj)) {
                        deleted_list.remove(i);
                    }
                }

                for(int i = 0; i < added_list.size(); i++) {
                    if(added_list.get(i).toString().equals(jj)) {
                        added_list.remove(i);

                    }
                }
                list.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i,list.size());

                image_num.setText(list.size() + " / 10");

            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected ImageView imageView;
        protected ImageButton delete;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageView = (ImageView) itemView.findViewById(R.id.upload_sale_image_rv);
            this.delete = (ImageButton) itemView.findViewById(R.id.delete_image_item);
        }
    }
}
