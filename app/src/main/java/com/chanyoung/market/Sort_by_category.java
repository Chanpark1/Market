package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Sort_by_category extends AppCompatActivity {

    RecyclerView rv;
    SortingAdapter adapter;
    LinearLayoutManager manager;

    List<sortedItems> list = new ArrayList<>();

    public String category;

    TextView tv_logo;
    TextView tv_none;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_by_category);

        tv_logo = (TextView) findViewById(R.id.sort_by_logo);
        tv_none = (TextView) findViewById(R.id.sort_by_none);

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

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            category = getIntent().getStringExtra("category");
            tv_logo.setText(category);
        }

        Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);
        Call<List<sortedItems>> call = retrofit_load_post.load_post_category(category);
        call.enqueue(new Callback<List<sortedItems>>() {
            @Override
            public void onResponse(Call<List<sortedItems>> call, Response<List<sortedItems>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    list = response.body();

                    generateDataList(list);
                }
            }

            @Override
            public void onFailure(Call<List<sortedItems>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                tv_none.setVisibility(View.VISIBLE);
            }
        });

    }

    private HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public void generateDataList(List<sortedItems> list) {
        rv = (RecyclerView) findViewById(R.id.sort_by_rv);

        adapter = new SortingAdapter(Sort_by_category.this,list);
        manager = new LinearLayoutManager(Sort_by_category.this);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
    }

}