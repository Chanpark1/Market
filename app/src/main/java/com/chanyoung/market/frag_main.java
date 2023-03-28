package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class frag_main extends Fragment {

    Button set_address;

    public String area;

    SharedPreferences prefs;

    FloatingActionButton floating;

    private View view;

    postAdapter adapter;
    LinearLayoutManager manager;
    RecyclerView rv;
    List<postItem> list;

    ImageButton cat_btn;
    ImageButton search_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main,container,false);
        Log.d(TAG,"onCreate");
        set_address = (Button) view.findViewById(R.id.main_change_address);
        cat_btn = (ImageButton) view.findViewById(R.id.frag_main_category);
        search_btn = (ImageButton) view.findViewById(R.id.frag_main_search);


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

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Search_sale_post.class);
                startActivity(intent);
            }
        });

        cat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Select_category.class);

                startActivity(intent);
            }
        });

        floating = (FloatingActionButton) view.findViewById(R.id.floating);

        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),upload_sale.class);
                startActivity(intent);
            }
        });

        prefs = getContext().getSharedPreferences("Username", Context.MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();
        Collection<?> col_key = prefs.getAll().keySet();
        Iterator<?> it_key = col_key.iterator();

        while(it_val.hasNext() && it_key.hasNext()) {
            String value = (String) it_val.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                String authNum = jsonObject.getString("authNum");

                Retrofit_req_address req_address = retrofit.create(Retrofit_req_address.class);
                Call<List<requestAddress>> request = req_address.req_address(authNum);
                request.enqueue(new Callback<List<requestAddress>>() {
                    @Override
                    public void onResponse(Call<List<requestAddress>> call, Response<List<requestAddress>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            for(int i = 0; i < response.body().size(); i++) {
                                String longitude  = response.body().get(i).getLongitude();
                                String latitude = response.body().get(i).getLatitude();
                                area = response.body().get(i).getArea();
                                Log.d("주소 : " , area);
                                set_address.setText(area);
                                prefs = getActivity().getSharedPreferences("savedState",Context.MODE_PRIVATE);

                                Collection<?> col_val = prefs.getAll().values();
                                Iterator<?> it_val = col_val.iterator();
                                Collection<?> col_key = prefs.getAll().keySet();
                                Iterator<?> it_key = col_key.iterator();

                                while(it_val.hasNext() && it_key.hasNext()) {
                                    String value = (String) it_val.next();

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(value);

                                        String distance = jsonObject1.getString("progress");

                                        Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);
                                        Call<List<postItem>> request_post = retrofit_load_post.load_post(longitude,latitude,distance);
                                        request_post.enqueue(new Callback<List<postItem>>() {
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

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<requestAddress>> call, Throwable t) {

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        set_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.chanyoung.market.set_address.class);
                startActivity(intent);

            }
        });
        return view;
    }

    private static HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public void generateDataList(List<postItem> list) {
        rv = view.findViewById(R.id.frag_main_posts);

        adapter = new postAdapter(view.getContext(),list);
        manager = new LinearLayoutManager(view.getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }
}
