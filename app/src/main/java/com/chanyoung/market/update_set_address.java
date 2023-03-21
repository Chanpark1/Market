package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup_location.SERVER_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class update_set_address extends AppCompatActivity {

    Location location;

    private updateAddressAdapter adapter;

    private RecyclerView recyclerView;

    Button current_location;

    EditText editText;

    updateAddressAdapter2 adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_set_address);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        current_location = (Button) findViewById(R.id.find_update_location);

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
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



        editText = (EditText) findViewById(R.id.update_location_search);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Retrofit_search_address retrofit_search_address = retrofit.create(Retrofit_search_address.class);

                String text = editText.getText().toString();

                Call<List<locationItem2>> listCall = retrofit_search_address.InsertText(text);
                listCall.enqueue(new Callback<List<locationItem2>>() {
                    @Override
                    public void onResponse(Call<List<locationItem2>> call, Response<List<locationItem2>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            generateDataList2(response.body());
                            System.out.println("텍스트 : " + response.body());

                        }
                    }

                    @Override
                    public void onFailure(Call<List<locationItem2>> call, Throwable t) {
                        System.out.println("텍스트 : "+ t.getMessage());
                    }
                });


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( update_set_address.this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                } else {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(location != null) {

                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();

                        String longitude_string = String.valueOf(longitude);
                        String latitude_string = String.valueOf(latitude);

                        Retrofit_location retrofit_location = retrofit.create(Retrofit_location.class);
                        Call<List<locationItem>> call = retrofit_location.InsertLocation(latitude_string,longitude_string);
                        call.enqueue(new Callback<List<locationItem>>() {
                            @Override
                            public void onResponse(Call<List<locationItem>> call, Response<List<locationItem>> response) {
                                if(response.isSuccessful() && response.body() != null) {

                                    generateDataList(response.body());
                                    System.out.println("버튼 : " +  response.body().toString());
                                }

                            }

                            @Override
                            public void onFailure(Call<List<locationItem>> call, Throwable t) {
                                System.out.println("버튼 : " + t.getMessage());
                            }
                        });

                    } else {
                        System.out.println("Unknown location");
                    }

                }

            }
        });

    }

    private void generateDataList(List<locationItem> locationItems) {
        recyclerView = findViewById(R.id.update_location_rv);

        adapter = new updateAddressAdapter(update_set_address.this,locationItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(update_set_address.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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

    public void generateDataList2(List<locationItem2> locationItems2) {
        recyclerView = findViewById(R.id.update_location_rv);

        adapter2 = new updateAddressAdapter2(update_set_address.this,locationItems2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(update_set_address.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter2);
    }
}