package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class frag_reserved extends Fragment {
    private View view;
    RecyclerView rv;
    LinearLayoutManager manager;
    SaleAdapter adapter;

    List<postItem> list = new ArrayList<>();

    public String authNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_reserved,container,false);

        SharedPreferences prefs = view.getContext().getSharedPreferences("Username", Context.MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            authNum = jsonObject.getString("authNum");

        } catch(JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);

        Call<List<postItem>> call = retrofit_load_post.reserved_post(authNum);

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
                Log.d(TAG, t.getMessage());
            }
        });



        return view;
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

    public void generateDataList(List<postItem> list) {
        rv = (RecyclerView) view.findViewById(R.id.frag_reserved_rv);

        adapter = new SaleAdapter(getActivity(),list);
        manager = new LinearLayoutManager(view.getContext());

        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);
    }

}
