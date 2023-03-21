package com.chanyoung.market;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PopAdapter extends RecyclerView.Adapter<PopAdapter.CustomViewHolder> {
    private Context context;
    List<communityItems> list;

    public PopAdapter(Context context, List<communityItems> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PopAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_post_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopAdapter.CustomViewHolder holder, int position) {

        String title = list.get(position).getTitle();

        holder.tv_title.setText(title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.pop_rv_title);
        }
    }
}
