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

public class frag_chatting extends Fragment {

    private View view;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    ChatListAdapter adapter;
    List<ChatList> list;

    public String authNum;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_chatting,container,false);

        Retrofit_chatting retrofit_chatting = getApiClient().create(Retrofit_chatting.class);
        Call<List<ChatList>> call = retrofit_chatting.load_chatList(getAuthNum());
        call.enqueue(new Callback<List<ChatList>>() {
            @Override
            public void onResponse(Call<List<ChatList>> call, Response<List<ChatList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list = response.body();

                    generateDataList(list);
                }
            }

            @Override
            public void onFailure(Call<List<ChatList>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

        return view;
    }

    private void generateDataList(List<ChatList> list) {
        recyclerView = (RecyclerView) view.findViewById(R.id.frag_chat_list);

        adapter = new ChatListAdapter(view.getContext(), list);
        manager = new LinearLayoutManager(view.getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

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

    private Retrofit getApiClient() {
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

        return retrofit;
    }

    private String getAuthNum() {
        SharedPreferences prefs = view.getContext().getSharedPreferences("Username", Context.MODE_PRIVATE);

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


}
