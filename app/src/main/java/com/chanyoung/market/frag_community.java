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
import android.widget.Button;
import android.widget.ImageButton;
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

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class frag_community extends Fragment {

    private View view;

    Button address;
    ImageButton search;
    ImageButton set_category;

    FloatingActionButton floating;

    RecyclerView pop_rv;
    LinearLayoutManager pop_manager;
    PopAdapter popAdapter;

    RecyclerView comm_rv;
    LinearLayoutManager comm_manager;
    CommAdapter commAdapter;

    TextView load_more;


    List<communityItems> list = new ArrayList<>();
    List<communityItems> pop_list = new ArrayList<>();

    public String authNum;
    public String distance;
    public String longitude;
    public String latitude;

    SharedPreferences prefs;
    SharedPreferences progress_prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_community,container,false);

        address = (Button) view.findViewById(R.id.comm_change_address);
        search = (ImageButton) view.findViewById(R.id.comm_main_search);
        set_category = (ImageButton) view.findViewById(R.id.comm_main_category);
        floating = (FloatingActionButton) view.findViewById(R.id.comm_floating);
        load_more = (TextView) view.findViewById(R.id.more_popular);

        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),upload_comm_post.class);

                startActivity(intent);
            }
        });

        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), popular_posts.class);

                startActivity(intent);
            }
        });


        Retrofit_req_address req_address = getApiClient().create(Retrofit_req_address.class);
        Call<List<requestAddress>> request = req_address.req_address(getAuthNum());

        request.enqueue(new Callback<List<requestAddress>>() {
            @Override
            public void onResponse(Call<List<requestAddress>> call, Response<List<requestAddress>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    for(int i = 0; i < response.body().size(); i++) {
                        longitude = response.body().get(i).getLongitude();
                        latitude = response.body().get(i).getLatitude();
                        String area = response.body().get(i).getArea();

                        address.setText(area);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<requestAddress>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
        Call<List<communityItems>> pop_posts = retrofit_load_post.pop_post(getAuthNum(),getDistance());
        pop_posts.enqueue(new Callback<List<communityItems>>() {
            @Override
            public void onResponse(Call<List<communityItems>> call, Response<List<communityItems>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    pop_list = response.body();

                    generatePopList(pop_list);
                }
            }

            @Override
            public void onFailure(Call<List<communityItems>> call, Throwable t) {
                Log.d("GeneratePopList",t.getMessage());
            }
        });

        Call<List<communityItems>> comm_posts = retrofit_load_post.comm_post(getAuthNum(),getDistance());
        comm_posts.enqueue(new Callback<List<communityItems>>() {
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

        return view;
    }

    public String getAuthNum() {
        prefs = view.getContext().getSharedPreferences("Username", Context.MODE_PRIVATE);

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
        progress_prefs = view.getContext().getSharedPreferences("savedState",Context.MODE_PRIVATE);

        Collection<?> pro_val = progress_prefs.getAll().values();
        Iterator<?> pro_it = pro_val.iterator();

        String pro = (String) pro_it.next();

        try {
            JSONObject jsonObject = new JSONObject(pro);

            distance = jsonObject.getString("progress");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return distance;
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

    private void generatePopList(List<communityItems> pop_list) {
        pop_rv = (RecyclerView) view.findViewById(R.id.comm_popular_rv);

        popAdapter = new PopAdapter(view.getContext(),pop_list);
        pop_manager = new LinearLayoutManager(view.getContext());

        pop_rv.setAdapter(popAdapter);
        pop_rv.setLayoutManager(pop_manager);
    }

    private void generateDataList(List<communityItems> list) {
        comm_rv = (RecyclerView) view.findViewById(R.id.comm_all_posts);

        commAdapter = new CommAdapter(view.getContext(),list);
        comm_manager = new LinearLayoutManager(view.getContext());

        comm_rv.setAdapter(commAdapter);
        comm_rv.setLayoutManager(comm_manager);

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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
    }

}
