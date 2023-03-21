package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.signup_location.SERVER_ADDRESS;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class set_address extends AppCompatActivity {

    RecyclerView recyclerView;

    addressAdapter addressAdapter;

    SharedPreferences prefs;

    ImageButton exit;

    SavedSharedPreferences preferences = new SavedSharedPreferences();

    Button add;

    public static int room;

    Parcelable rv_state;

    SeekBar seekBar;

    private final int min = 1;
    private final int max = 5;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);


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



        seekBar = findViewById(R.id.seekBar);
        seekBar.setMin(min);
        seekBar.setMax(max);

        prefs = getSharedPreferences("savedState",MODE_PRIVATE);
        if(prefs != null) {

            Collection<?> col_val = prefs.getAll().values();
            Iterator<?> it_val = col_val.iterator();
            Collection<?> col_key = prefs.getAll().keySet();
            Iterator<?> it_key = col_key.iterator();

            while(it_val.hasNext() && it_key.hasNext()) {
                String value = (String) it_val.next();

                try {
                    JSONObject jsonObject = new JSONObject(value);

                    String progress_String = jsonObject.getString("progress");

                    int progress = Integer.parseInt(progress_String);
                    seekBar.setProgress(progress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //TODO 드래그 멈추면 발생하는 이벤트
                Log.d(TAG, String.valueOf(i));



                String save_form = "{\"progress\":\""+String.valueOf(i)+"\"}";
                preferences.clearSavedState(set_address.this,"savedState");
                preferences.save_state(set_address.this, "savedState",save_form);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO 최초 탭하여 드래그 시작 시 발생


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO 드래그 중 발생

            }
        });


        add = (Button) findViewById(R.id.add);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(set_address.this, set_address_location.class);
                startActivity(intent);
            }
        });



                prefs = getSharedPreferences("Username",MODE_PRIVATE);
                Collection<?> col_val = prefs.getAll().values();
                Iterator<?> it_val = col_val.iterator();
                Collection<?> col_key = prefs.getAll().keySet();
                Iterator<?> it_key = col_key.iterator();

                while(it_val.hasNext() && it_key.hasNext()) {
                    String value = (String) it_val.next();
                    String key = (String) it_key.next();

                    try {
                        JSONObject jsonObject = new JSONObject(value);

                        String authNum = jsonObject.getString("authNum");
                        System.out.println(authNum);

                        Retrofit_set_address retrofit_set_address = retrofit.create(Retrofit_set_address.class);

                        Call<List<addressItem>> call = retrofit_set_address.insertQuery(authNum);
                        call.enqueue(new Callback<List<addressItem>>() {
                            @Override
                            public void onResponse(Call<List<addressItem>> call, Response<List<addressItem>> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    generateDataList(response.body());
                                    room = response.body().size();
                                    System.out.println(room);

                                    if(room < 2 ) {
                                        add.setVisibility(View.VISIBLE);
                                    } else if (room == 2) {
                                        add.setVisibility(View.GONE);
                                    }

                                    System.out.println(room);
                                    System.out.println(response.body().toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<addressItem>> call, Throwable t) {
                                System.out.println("실패 : " + t.getMessage());
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                exit = (ImageButton) findViewById(R.id.set_address_exit);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(set_address.this,MainActivity.class);
                        prefs = getSharedPreferences("savedState", Context.MODE_PRIVATE);

                        Collection<?> col_val = prefs.getAll().values();
                        Iterator<?> it_val = col_val.iterator();
                        Collection<?> col_key = prefs.getAll().keySet();
                        Iterator<?> it_key = col_key.iterator();

                        while(it_val.hasNext() && it_key.hasNext()) {
                            String value = (String) it_val.next();

                            try {
                                JSONObject jsonObject1 = new JSONObject(value);

                                String progress = jsonObject1.getString("progress");


                                intent.putExtra("distance", progress);

                                startActivity(intent);

                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

            }
    private HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private void generateDataList(List<addressItem> addressItems) {
        recyclerView = findViewById(R.id.set_address_rv);

        addressAdapter = new addressAdapter(set_address.this,addressItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(set_address.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(addressAdapter);
    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

