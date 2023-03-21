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

public class wishlist extends AppCompatActivity {

    WishAdapter adapter;
    LinearLayoutManager manager;
    RecyclerView rv;
    List<postItem> list = new ArrayList<>();

    public String authNum;
    public String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

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

        SharedPreferences prefs_auth = getSharedPreferences("Username",MODE_PRIVATE);

        Collection<?> col_val = prefs_auth.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        while(it_val.hasNext()) {
            String value = (String) it_val.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                authNum = jsonObject.getString("authNum");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        SharedPreferences prefs_dis = getSharedPreferences("savedState",MODE_PRIVATE);

        Collection<?> dis_val = prefs_dis.getAll().values();
        Iterator<?> it_dis = dis_val.iterator();

        while(it_dis.hasNext()) {
            String value = (String) it_dis.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                distance = jsonObject.getString("progress");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        Call<List<postItem>> call = retrofit_load_post.load_post_simplified(authNum,distance);
        call.enqueue(new Callback<List<postItem>>() {
            @Override
            public void onResponse(Call<List<postItem>> call, Response<List<postItem>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    list = response.body();
                    generateDataList(list);
                }
            }

            @Override
            public void onFailure(Call<List<postItem>> call, Throwable t) {
                Log.d(TAG,t.getMessage());
            }
        });


    }

    private static HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public void generateDataList(List<postItem> list) {
        rv = (RecyclerView) findViewById(R.id.wishlist_rv);

        adapter = new WishAdapter(wishlist.this,list);
        manager = new LinearLayoutManager(wishlist.this);

        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);

    }


}