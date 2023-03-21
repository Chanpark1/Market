package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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

public class Search_sale_post extends AppCompatActivity {

    RecyclerView rv;
    postAdapter adapter;
    LinearLayoutManager manager;

    EditText input_search;
    List<postItem> list;

    public String area;
    public String authNum;
    public String progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sale_post);

        input_search = (EditText) findViewById(R.id.search_sale_input);

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

        SharedPreferences prefs = getSharedPreferences("Username",MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        while(it_val.hasNext()) {
            String value = (String) it_val.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                authNum = jsonObject.getString("authNum");

            } catch (JSONException e){
                e.printStackTrace();
            }

        }

        SharedPreferences preferences = getSharedPreferences("savedState",MODE_PRIVATE);

        Collection<?> val = preferences.getAll().values();
        Iterator<?> it = val.iterator();

        while(it.hasNext()) {
            String value = (String) it.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                progress = jsonObject.getString("progress");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);

                String text = input_search.getText().toString();
                Call<List<postItem>> call = retrofit_load_post.search_post(text,progress,authNum);

                call.enqueue(new Callback<List<postItem>>() {
                    @Override
                    public void onResponse(Call<List<postItem>> call, Response<List<postItem>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            generateDataList(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<postItem>> call, Throwable t) {
                        Log.d("실패",t.getMessage());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        Retrofit_upload_check_auth ca = retrofit.create(Retrofit_upload_check_auth.class);

        Call<String> check = ca.checkAuth(authNum);
        check.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null) {
                    area = response.body();

                    input_search.setHint(area + " 근처에서 검색");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });







    }

    public void generateDataList(List<postItem> list) {
        rv = (RecyclerView) findViewById(R.id.search_sale_rv);

        adapter = new postAdapter(Search_sale_post.this,list);
        manager = new LinearLayoutManager(Search_sale_post.this);

        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
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
 }