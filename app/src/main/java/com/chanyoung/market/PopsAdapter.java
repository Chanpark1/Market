package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.Chrono.timesAgo;
import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PopsAdapter extends RecyclerView.Adapter<PopsAdapter.CustomViewHolder> {

    List<communityItems> list;
    private final Context context;

    public PopsAdapter(Context context, List<communityItems> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PopsAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comm_post_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopsAdapter.CustomViewHolder holder, int position) {

        LocalDateTime dateTime = null;


        String title = list.get(position).getTitle();
        String category = list.get(position).getCategory();
        String area = list.get(position).getArea();
        String created = list.get(position).getCreated();
        String like_num = list.get(position).getLike();
        String rep_num = list.get(position).getRep_num();
        String path = list.get(position).getPath();
        String post_auth = list.get(position).getPost_authNum();
        String authNum = list.get(position).getAuthNum();

        if(path != null) {
            Glide.with(context)
                    .load(path)
                    .into(holder.image);
        }

        if(!rep_num.equals("0")) {
            holder.rep_icon.setVisibility(View.VISIBLE);
            holder.tv_rep.setVisibility(View.VISIBLE);
            holder.tv_rep.setText(rep_num);
        }

        if(!like_num.equals("0")) {
            holder.like_icon.setVisibility(View.VISIBLE);
            holder.tv_like.setVisibility(View.VISIBLE);
            holder.tv_like.setText(like_num);
        }


        if(created != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime parse = LocalDateTime.parse(created,format);
                String final_date = timesAgo(parse);
                holder.tv_created.setText(final_date);
            }
        }

        holder.tv_title.setText(title);
        holder.tv_cat.setText(category);
        holder.tv_area.setText(area);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), comm_post_read.class);
                intent.putExtra("authNum",authNum);
                intent.putExtra("post_authNum",post_auth);
                intent.putExtra("category", category);

                Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
                Call<String> call = retrofit_load_post.update_hit(post_auth);

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

    public class CustomViewHolder extends  RecyclerView.ViewHolder {

        protected TextView tv_title;
        protected TextView tv_cat;
        protected TextView tv_area;
        protected TextView tv_created;
        protected TextView tv_like;
        protected TextView tv_rep;
        protected ImageView like_icon;
        protected ImageView rep_icon;
        protected ImageView image;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.comm_rv_title);
            this.tv_cat = (TextView) itemView.findViewById(R.id.comm_rv_category);
            this.tv_area = (TextView) itemView.findViewById(R.id.comm_rv_area);
            this.tv_created = (TextView) itemView.findViewById(R.id.comm_rv_created);
            this.image = (ImageView) itemView.findViewById(R.id.comm_rv_iv);
            this.like_icon = (ImageView) itemView.findViewById(R.id.comm_rv_like_image);
            this.rep_icon = (ImageView) itemView.findViewById(R.id.comm_rv_rep_image);
            this.tv_like = (TextView) itemView.findViewById(R.id.comm_rv_like_num);
            this.tv_rep = (TextView) itemView.findViewById(R.id.comm_rv_reply_num);

        }
    }
}
