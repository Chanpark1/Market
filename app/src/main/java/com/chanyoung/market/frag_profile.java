package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;
import static com.chanyoung.market.signup_profile.url;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class frag_profile extends Fragment {

    private View view;
    Button logout;
    Button edit;

    LinearLayout linear_wishlist;
    LinearLayout linear_soldlist;

    TextView tv_name;
    ImageView profile;

    List<profileItems> list = new ArrayList<>();

    public String authNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_profile,container,false);
        linear_wishlist = (LinearLayout) view.findViewById(R.id.info_linear3);
        linear_soldlist = (LinearLayout) view.findViewById(R.id.info_linear1);
        edit = (Button) view.findViewById(R.id.my_profile);



        tv_name = (TextView) view.findViewById(R.id.info_name);
        profile = (ImageView) view.findViewById(R.id.info_image);

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

        SharedPreferences prefs = view.getContext().getSharedPreferences("Username",Context.MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            authNum = jsonObject.getString("authNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Retrofit_basic basic = retrofit.create(Retrofit_basic.class);
        Call<List<profileItems>> call = basic.load_profile(authNum);

        call.enqueue(new Callback<List<profileItems>>() {
            @Override
            public void onResponse(Call<List<profileItems>> call, Response<List<profileItems>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    list = response.body();

                    for(int i = 0; i < list.size(); i++) {
                        String username = list.get(i).getUsername();
                        String path = list.get(i).getPath();

                        tv_name.setText(username);

                        if(path == null ) {
                            Glide.with(view.getContext())
                                    .load(url)
                                    .into(profile);
                        } else {
                            Glide.with(view.getContext())
                                    .load(path)
                                    .into(profile);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<profileItems>> call, Throwable t) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),edit_profile.class);
                startActivity(intent);
            }
        });




        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?");

                builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SavedSharedPreferences pref = new SavedSharedPreferences();

                        pref.clearUserName(getActivity(),"Username");
                        pref.clearSavedState(getActivity(),"savedState");

                        Intent intent = new Intent(getActivity(),FirstActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        linear_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),wishlist.class);
                startActivity(intent);
            }
        });

        linear_soldlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),sold_list.class);
                startActivity(intent);
            }
        });

        return view;
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


}
