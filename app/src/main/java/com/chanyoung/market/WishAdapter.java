package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WishAdapter extends RecyclerView.Adapter<WishAdapter.CustomViewHolder> {

    Context context;
    List<postItem> list;

    public WishAdapter(Context context, List<postItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WishAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_post_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WishAdapter.CustomViewHolder holder, int position) {
        String area = list.get(position).getArea();
        String title = list.get(position).getTitle();
        String price = list.get(position).getPrice();
        String like = list.get(position).getLike();
        String chat = list.get(position).getChat_num();
        String image_uri = list.get(position).getImage_uri();
        String post_authNum = list.get(position).getPost_authNum();
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
                Intent intent = new Intent(view.getContext(),wishlist_read.class);
                intent.putExtra("trade_lat",tr_lat);
                intent.putExtra("trade_lng",tr_lng);
                intent.putExtra("post_authNum",post_authNum);
                intent.putExtra("status",status);

                view.getContext().startActivity(intent);
                ((wishlist)context).finish();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor())
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();

                Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);
                Call<String> call = retrofit_load_post.up_hit(post_authNum);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView tv_title;
        protected TextView tv_area;
        protected TextView tv_price;
        protected TextView tv_like;
        protected TextView tv_chat;
        protected TextView tv_time;
        protected TextView reserved;

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

    private HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
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
