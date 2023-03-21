package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.Chrono.timesAgo;
import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

public class CommAdapter extends RecyclerView.Adapter<CommAdapter.CustomViewHolder> {

    private Context context;
    List<communityItems> list;

    public CommAdapter(Context context, List<communityItems> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comm_post_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CommAdapter.CustomViewHolder holder, int position) {

        String title = list.get(position).getTitle();
        String cat = list.get(position).getCategory();
        String area = list.get(position).getArea();
        String created = list.get(position).getCreated();
        String rep_num = list.get(position).getRep_num();
        String image_path = list.get(position).getPath();
        String like_num = list.get(position).getLike();
        String authNum = list.get(position).getAuthNum();
        String post_authNum = list.get(position).getPost_authNum();
        String category = list.get(position).getCategory();



        if(!rep_num.equals("0")) {
            holder.rep_icon.setVisibility(View.VISIBLE);
            holder.tv_rep_num.setVisibility(View.VISIBLE);
            holder.tv_rep_num.setText(rep_num);
        }

        if(!like_num.equals("0")) {
            holder.like_icon.setVisibility(View.VISIBLE);
            holder.tv_like_num.setVisibility(View.VISIBLE);
            holder.tv_like_num.setText(like_num);
        }

        holder.tv_title.setText(title);
        holder.tv_category.setText(cat);
        holder.tv_area.setText(area);
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
           LocalDateTime localDateTime = LocalDateTime.parse(created,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
           String time = timesAgo(localDateTime);
           holder.tv_created.setText(time);
        }

        holder.iv_image.setClipToOutline(true);

        if(image_path != null) {
            holder.iv_image.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(image_path);
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .into(holder.iv_image);
        }else {
            holder.iv_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),comm_post_read.class);
                intent.putExtra("authNum",authNum);
                intent.putExtra("post_authNum",post_authNum);
                intent.putExtra("category",category);

                Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
                Call<String> call = retrofit_load_post.update_hit(post_authNum);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            view.getContext().startActivity(intent);
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

    private Retrofit getApiClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
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

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;
        protected TextView tv_category;
        protected TextView tv_area;
        protected TextView tv_created;
        protected TextView tv_rep_num;
        protected ImageView rep_icon;
        protected ImageView iv_image;
        protected ImageView like_icon;
        protected TextView tv_like_num;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.comm_rv_title);
            this.tv_category = (TextView) itemView.findViewById(R.id.comm_rv_category);
            this.tv_area = (TextView) itemView.findViewById(R.id.comm_rv_area);
            this.tv_created =(TextView) itemView.findViewById(R.id.comm_rv_created);
            this.tv_rep_num =(TextView) itemView.findViewById(R.id.comm_rv_reply_num);
            this.rep_icon = (ImageView) itemView.findViewById(R.id.comm_rv_rep_image);
            this.iv_image = (ImageView) itemView.findViewById(R.id.comm_rv_iv);
            this.like_icon = (ImageView) itemView.findViewById(R.id.comm_rv_like_image);
            this.tv_like_num = (TextView) itemView.findViewById(R.id.comm_rv_like_num);


        }
    }
}
