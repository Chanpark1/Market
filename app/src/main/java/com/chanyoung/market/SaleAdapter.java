package com.chanyoung.market;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.CustomViewHolder> {

    private Context context;
    List<postItem> list;

    public SaleAdapter(Context context, List<postItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SaleAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_post_rv, parent, false);

        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull SaleAdapter.CustomViewHolder holder, int position) {

        String area = list.get(position).getArea();
        String title = list.get(position).getTitle();
        String price = list.get(position).getPrice();
        String like = list.get(position).getLike();
        String chat = list.get(position).getChat_num();
        String image_uri = list.get(position).getImage_uri();
        String post_authNum = list.get(position).getPost_authNum();
        String authNum = list.get(position).getAuthNum();
        String tr_lat = list.get(position).getTr_lat();
        String tr_lng = list.get(position).getTr_lng();
        String date = list.get(position).getCreated();
        String status = list.get(position).getStatus();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        try {
            Date mDate = format.parse(date);

            String stDate = formatTimeString(mDate);
            holder.tv_time.setText(stDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.imageView.setClipToOutline(true);

        if(image_uri != null) {
            Uri uri = Uri.parse(image_uri);
            Glide.with(context.getApplicationContext())
                    .load(uri)
                    .into(holder.imageView);
        }

        if(status.equals("예약중")) {
            holder.reserved.setVisibility(View.VISIBLE);
        }

        holder.tv_title.setText(title);
        holder.tv_area.setText(area);
        holder.tv_price.setText(price+"원");
        holder.tv_like.setText(like);
        holder.tv_chat.setText(chat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(),sold_list_read.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("post_authNum",post_authNum);
                // 선택한 게시물에 맞는 정보를 뿌려주기 위해 식별키를 보내줌
                intent.putExtra("trade_lat",tr_lat);
                intent.putExtra("trade_lng",tr_lng);
                intent.putExtra("status",status);
                intent.putExtra("authNum",authNum);

                view.getContext().startActivity(intent);
                ((sold_list)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView reserved;
        protected TextView tv_title;
        protected TextView tv_area;
        protected TextView tv_price;
        protected TextView tv_like;
        protected TextView tv_chat;
        protected TextView tv_time;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageView = (ImageView) itemView.findViewById(R.id.main_post_image);
            this.tv_title = (TextView) itemView.findViewById(R.id.main_post_title);
            this.tv_area = (TextView) itemView.findViewById(R.id.main_post_location);
            this.tv_price = (TextView) itemView.findViewById(R.id.main_post_price);
            this.tv_like = (TextView) itemView.findViewById(R.id.like_num);
            this.tv_chat = (TextView) itemView.findViewById(R.id.chat_num);
            this.tv_time = (TextView) itemView.findViewById(R.id.main_post_time);
            this.reserved = (TextView) itemView.findViewById(R.id.isReserved);
        }
    }

    public String formatTimeString(Date mDate) {
        long curTime = System.currentTimeMillis();

        long reqTime = mDate.getTime();

        long diffTime = (curTime - reqTime) / 1000;
        String msg = null;


        if (diffTime < postAdapter.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.SEC) < postAdapter.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.MIN) < postAdapter.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.HOUR) < postAdapter.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.DAY) < postAdapter.TIME_MAXIMUM.MONTH ) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
