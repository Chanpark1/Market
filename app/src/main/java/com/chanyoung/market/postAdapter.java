package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.Chrono.timesAgo;
import static com.chanyoung.market.login.SERVER_ADDRESS;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class postAdapter extends RecyclerView.Adapter<postAdapter.CustomViewHolder> {

private List<postItem> list;
private Context mContext;
public String user_auth = null;
FragmentManager fragmentManager;

public static class TIME_MAXIMUM {
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;
}

public postAdapter(Context mContext, List<postItem> list) {
    this.mContext = mContext;
    this.list = list;
}

public String formatTimeString(Date mDate) {
    long curTime = System.currentTimeMillis();

    long reqTime = mDate.getTime();

    long diffTime = (curTime - reqTime) / 1000;
    String msg = null;


    if (diffTime < TIME_MAXIMUM.SEC) {
        msg = "방금 전";
    } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
        msg = diffTime + "분 전";
    } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
        msg = (diffTime) + "시간 전";
    } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
        msg = (diffTime) + "일 전";
    } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH ) {
        msg = (diffTime) + "달 전";
    } else {
        msg = (diffTime) + "년 전";
    }
    return msg;
}

    @NonNull
    @Override
    public postAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_post_rv,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull postAdapter.CustomViewHolder holder, int position) {

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

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(date, format);

        String final_date = timesAgo(parse);
        holder.tv_time.setText(final_date);
    }

        holder.imageView.setClipToOutline(true);
    if(image_uri != null) {
        Uri uri = Uri.parse(image_uri);
        Glide.with(mContext.getApplicationContext())
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

                Intent intent = new Intent(view.getContext(),upload_sale_read.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("post_authNum",post_authNum);
                // 선택한 게시물에 맞는 정보를 뿌려주기 위해 식별키를 보내줌
                intent.putExtra("trade_lat",tr_lat);
                intent.putExtra("trade_lng",tr_lng);
                intent.putExtra("status",status);
                intent.putExtra("authNum",authNum);


                view.getContext().startActivity(intent);
                ((MainActivity)mContext).finish();


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


                SharedPreferences prefs = mContext.getSharedPreferences("Username",Context.MODE_PRIVATE);

                Collection<?> col_val = prefs.getAll().values();
                Iterator<?> it_val = col_val.iterator();

                while(it_val.hasNext()) {
                    String value = (String) it_val.next();

                    try {
                        JSONObject jsonObject = new JSONObject(value);

                        user_auth = jsonObject.getString("authNum");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);
                Call<String> call = retrofit_load_post.up_hit(post_authNum);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });



            }
        });
    }

    private HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
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


}
