package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
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

public class popular_posts extends AppCompatActivity {

    private String authNum;
    private String distance;

    RecyclerView recyclerView;
    PopsAdapter adapter;
    LinearLayoutManager manager;
    List<communityItems> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_posts);

        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);

        Call<List<communityItems>> call = retrofit_load_post.all_pop(getAuthNum(), getDistance());
        call.enqueue(new Callback<List<communityItems>>() {
            @Override
            public void onResponse(Call<List<communityItems>> call, Response<List<communityItems>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    list = response.body();

                    generateDataList(list);
                }
            }

            @Override
            public void onFailure(Call<List<communityItems>> call, Throwable t) {

            }
        });
    }


    private void generateDataList(List<communityItems> list) {
        recyclerView = (RecyclerView) findViewById(R.id.pop_rv);

        adapter = new PopsAdapter(popular_posts.this, list);
        manager = new LinearLayoutManager(popular_posts.this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    private Retrofit getApiClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
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



    public String getAuthNum() {

        SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            authNum = jsonObject.getString("authNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return authNum;
    }

    public String getDistance() {
        SharedPreferences prefs = getSharedPreferences("savedState", MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            distance = jsonObject.getString("progress");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return distance;
    }
}