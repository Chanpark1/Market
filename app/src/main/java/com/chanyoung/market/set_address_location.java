package com.chanyoung.market;

import static com.chanyoung.market.signup_location.SERVER_ADDRESS;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class set_address_location extends AppCompatActivity {

    private locationAdapter locationAdapter;
    private locationAdapter2 locationAdapter2;
    private RecyclerView recyclerView;
    Button current_location;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    EditText search_input;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address_location);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        search_input = (EditText) findViewById(R.id.set_location_search);

        if(!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Retrofit_search_address retrofit_search_address = retrofit.create(Retrofit_search_address.class);

                String text = search_input.getText().toString();

                Call<List<locationItem2>> listCall = retrofit_search_address.InsertText(text);
                listCall.enqueue(new Callback<List<locationItem2>>() {
                    @Override
                    public void onResponse(Call<List<locationItem2>> call, Response<List<locationItem2>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            generateDataList2(response.body());

                        }
                    }
                    @Override
                    public void onFailure(Call<List<locationItem2>> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        current_location = (Button) findViewById(R.id.find_set_location);
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( set_address_location.this, new String[] {
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
                                    System.out.println("onResponse" + response.body());
                                }
                            }
                            @Override
                            public void onFailure(Call<List<locationItem>> call, Throwable t) {
                                System.out.println("onFailure : " +  t.getMessage());
                            }
                        });

                    } else {
                        System.out.println("Unknown location");
                    }

                }
            }
        });


    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(set_address_location.this);
        builder.setTitle("위치 서비스 비활성화;");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다. \n"
                + "위치 설정을 수정 하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE :

                if(checkLocationServicesStatus()) {
                    if(checkLocationServicesStatus()) {
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(set_address_location.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(set_address_location.this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(set_address_location.this,REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(this, "위치 권한을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(set_address_location.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(set_address_location.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void generateDataList(List<locationItem> results) {
        recyclerView = findViewById(R.id.set_location_rv);

        address_locationAdapter  address_locationAdapter = new address_locationAdapter(set_address_location.this,results);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(set_address_location.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(address_locationAdapter);
    }

    public void generateDataList2(List<locationItem2> locationItems2) {
        recyclerView = findViewById(R.id.set_location_rv);

        address_locationAdapter2 address_locationAdapter2 = new address_locationAdapter2(set_address_location.this,locationItems2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(set_address_location.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(address_locationAdapter2);
    }

}