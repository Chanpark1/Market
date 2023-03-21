package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static android.preference.PreferenceManager.*;

import static com.chanyoung.market.signup_location.SERVER_ADDRESS;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class upload_sale extends AppCompatActivity implements OnMapReadyCallback {

    Button exit;
    Button upload;
    ImageButton imageButton;

    Spinner spinner = null;
    ArrayAdapter<CharSequence> spinner_adapter = null;

    EditText input_title;
    EditText input_price;
    EditText input_description;

    SharedPreferences prefs;

    boolean hasCamPerm;
    boolean hasWritePerm;
    ArrayList<Uri> list = new ArrayList<>();

    RecyclerView rv;
    imageAdapter adapter;
    LinearLayoutManager manager;

    TextView image_num;

    Button trade;

    public MapView mapView;
    public NaverMap naverMap;

    public double lat;
    public double lng;

    public String category;

    public String prefs_title;
    public String prefs_content;
    public String prefs_price;

    SavedSharedPreferences preferences = new SavedSharedPreferences();

    JSONArray jsonArray;

    List<requestAddress> request_items;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_sale);
        mapView = (MapView) findViewById(R.id.upload_map_view);
        System.out.println("On Create  리스트 사이즈 " + list.size());

        input_title = (EditText) findViewById(R.id.upload_sale_title);
        input_price = (EditText) findViewById(R.id.upload_sale_price);
        input_description = (EditText) findViewById(R.id.upload_sale_description);
        upload = (Button) findViewById(R.id.upload_button);
        imageButton = (ImageButton) findViewById(R.id.upload_sale_image);
        image_num = (TextView) findViewById(R.id.image_num);
        trade = (Button) findViewById(R.id.upload_sale_location);

        spinner = (Spinner) findViewById(R.id.upload_sale_category);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.category, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(upload_sale.this, trade_location.class);
                startActivity(intent);
            }
        });


            String tr_lat = getIntent().getStringExtra("trade_lat");
            String tr_lng = getIntent().getStringExtra("trade_lng");
        if(tr_lat != null && tr_lng != null) {
            lat = Double.parseDouble(tr_lat);
            lng = Double.parseDouble(tr_lng);

            mapView.getMapAsync(upload_sale.this);
            mapView.setVisibility(View.VISIBLE);

        }

        hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        rv = (RecyclerView) findViewById(R.id.upload_sale_rv);
        adapter = new imageAdapter(upload_sale.this,list);
        manager = new LinearLayoutManager(upload_sale.this,LinearLayoutManager.HORIZONTAL,false);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(upload_sale.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(upload_sale.this);

                builder.setTitle("사진 선택");

                builder.setItems(R.array.NAL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {

                            case 0 :
                                Gallery();
                                break;

                            case 1 :
                                TakePhoto();
                                break;

                            case 2 :
                                dialogInterface.dismiss();

                        }

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

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

        prefs = getSharedPreferences("Username",MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        while(it_val.hasNext()) {
            String value = (String) it_val.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                String authNum = jsonObject.getString("authNum");

                Retrofit_upload_check_auth retro = retrofit.create(Retrofit_upload_check_auth.class);

                Call<String> call = retro.checkAuth(authNum);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            String area = response.body();

                            input_description.setHint(area + "에 올릴 게시글 내용을 작성해주세요.");

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preferences.clearUri(upload_sale.this,"uri");
                preferences.clearPost(upload_sale.this,"post");

                prefs = getSharedPreferences("Username",MODE_PRIVATE);

                Collection<?> col_val1 = prefs.getAll().values();
                Iterator<?> it_val1 = col_val1.iterator();
                Collection<?> col_key1 = prefs.getAll().keySet();
                Iterator<?> it_key1 = col_key1.iterator();

                while(it_val1.hasNext() && it_key1.hasNext()) {
                    String value = (String) it_val1.next();

                    try {
                        JSONObject jsonObject = new JSONObject(value);

                        String authNum = jsonObject.getString("authNum");

                        Retrofit_load_post posts = retrofit.create(Retrofit_load_post.class);

                        Retrofit_req_address req_address = retrofit.create(Retrofit_req_address.class);

                        Call<List<requestAddress>> requestAddressCall = req_address.req_address(authNum);

                        requestAddressCall.enqueue(new Callback<List<requestAddress>>() {
                            @Override
                            public void onResponse(Call<List<requestAddress>> call, Response<List<requestAddress>> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    request_items = response.body();
                                    for(int i = 0; i < request_items.size(); i++) {
                                        String area = request_items.get(i).getArea();
                                        String longitude = request_items.get(i).getLongitude();
                                        String latitude = request_items.get(i).getLatitude();
                                        String title = input_title.getText().toString();
                                        String price = input_price.getText().toString();
                                        String description = input_description.getText().toString();


                                        RequestBody title_body = RequestBody.create(MediaType.parse("text/plain"),title);
                                        RequestBody price_body = RequestBody.create(MediaType.parse("text/plain"),price);
                                        RequestBody des_body = RequestBody.create(MediaType.parse("text/plain"),description);
                                        RequestBody auth_body = RequestBody.create(MediaType.parse("text/plain"),authNum);
                                        RequestBody area_body = RequestBody.create(MediaType.parse("text/plain"),area);
                                        RequestBody long_body = RequestBody.create(MediaType.parse("text/plain"),longitude);
                                        RequestBody lat_body = RequestBody.create(MediaType.parse("text/plain"),latitude);
                                        RequestBody cat_body = RequestBody.create(MediaType.parse("text/plain"),category);

                                        RequestBody count_body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(list.size()));

                                        HashMap<String,RequestBody> hashMap = new HashMap<>();
                                        hashMap.put("title",title_body);
                                        hashMap.put("price",price_body);
                                        hashMap.put("description",des_body);
                                        hashMap.put("authNum",auth_body);
                                        hashMap.put("area",area_body);
                                        hashMap.put("longitude",long_body);
                                        hashMap.put("latitude",lat_body);
                                        hashMap.put("category",cat_body);

                                        hashMap.put("count",count_body);

                                        Bundle extras = getIntent().getExtras();

                                        if(extras != null) {
                                            String trade_lat = getIntent().getStringExtra("trade_lat");
                                            String trade_lng = getIntent().getStringExtra("trade_lng");

                                            RequestBody tr_lat_body = RequestBody.create(MediaType.parse("text/plain"),trade_lat);
                                            RequestBody tr_lng_body = RequestBody.create(MediaType.parse("text/plain"),trade_lng);

                                            hashMap.put("trade_lat",tr_lat_body);
                                            hashMap.put("trade_lng",tr_lng_body);
                                        }


                                        ArrayList<MultipartBody.Part> files = new ArrayList<>();

                                        for(int a = 0; a < list.size(); a++) {
                                            // list = uri image list for recyclerview.

                                            Uri uri  = list.get(a);

                                            String path = getAbsolutePath(uri);

                                            File file = new File(path);

                                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);

                                            String fileName = "photo" + a + ".jpg";

                                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file"+a,fileName, fileBody);

                                            System.out.println(list.get(i));

                                            files.add(filePart);
                                        }


                                        Call<String> uploadPost = posts.uploadPost(files,hashMap);
                                        uploadPost.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if(response.isSuccessful() && response.body() != null) {

                                                    Intent intent = new Intent(upload_sale.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.d("upload_post",t.getMessage());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<requestAddress>> call, Throwable t) {
                                Log.d("다시해",t.getMessage());
                            }
                        });



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

    public void Gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,333);
    }


    public void TakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent,222);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 333) {
            if(data == null) {
                Toast.makeText(this, "이미지를 선택 해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                if(data.getClipData() == null) {
                    Uri imageUri = data.getData();
                    list.add(imageUri);
                } else {
                    ClipData clipData = data.getClipData();
                    if(clipData.getItemCount() > 10) {
                        Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        for(int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();

                            try {
                                list.add(imageUri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        adapter = new imageAdapter(upload_sale.this,list);
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(manager);

                        image_num.setText(list.size() + " / 10");


                    }
                }
            }
        }
    }

    private String getAbsolutePath(Uri uri) {

        String result;
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        Bundle extras = getIntent().getExtras();

        if(extras != null) {

            String tr_lat = getIntent().getStringExtra("trade_lat");
            String tr_lng = getIntent().getStringExtra("trade_lng");

            lat = Double.parseDouble(tr_lat);
            lng = Double.parseDouble(tr_lng);

            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(lat,lng), 15
            );
            naverMap.setCameraPosition(cameraPosition);

            Marker marker = new Marker();
            setMark(marker, lat,lng, R.drawable.ic_baseline_location_on_24);

        }

    }

    private void setMark(Marker marker, double lat, double lng, int resource) {
        marker.setIconPerspectiveEnabled(true);
        // 원근감 표시

        marker.setIcon(OverlayImage.fromResource(resource));
        // 마커 아이콘 지정

        marker.setAlpha(1);
        // 마커 투명도 지정

        marker.setHeight(130);
        marker.setWidth(130);

        marker.setPosition(new LatLng(lat,lng));
        //마커 위치 지정
        marker.setMap(naverMap);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("post",MODE_PRIVATE);

        if(prefs != null) {
            Collection<?> col_val = prefs.getAll().values();
            Iterator<?> it_val = col_val.iterator();

            while(it_val.hasNext()) {
                String value = (String) it_val.next();

                try {
                    JSONObject jsonObject = new JSONObject(value);

                    input_title.setText(jsonObject.getString("title"));
                    input_price.setText(jsonObject.getString("price"));
                    input_description.setText(jsonObject.getString("content"));

                    preferences.clearPost(upload_sale.this, "post");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
         }

        SharedPreferences uri_prefs = getSharedPreferences("uri", MODE_PRIVATE);

        if(uri_prefs != null) {

            String json = uri_prefs.getString("uri","");

            try {
                JSONArray jsons = new JSONArray(json);

                for (int i = 0; i < jsons.length(); i++) {

                    String str_uri = jsons.get(i).toString();

                    Uri uri = Uri.parse(str_uri);

                    list.add(uri);

                    adapter = new imageAdapter(upload_sale.this,list);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(manager);

                    image_num.setText(list.size() + " / 10");

                    preferences.clearUri(upload_sale.this,"uri");


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        prefs_title = input_title.getText().toString();
        prefs_price = input_price.getText().toString();
        prefs_content = input_description.getText().toString();

        String save_form = "{\"title\":\""+prefs_title+"\",\"price\":\""+prefs_price+"\",\"content\":\""+prefs_content+"\"}";
        preferences.clearPost(upload_sale.this,"post");
        preferences.setPost(upload_sale.this,"post",save_form);

        jsonArray = new JSONArray();
        for(int i = 0 ;i < list.size(); i++) {
            jsonArray.put(list.get(i));
        }
        preferences.clearUri(upload_sale.this,"uri");
        preferences.setUri(upload_sale.this,"uri",jsonArray);

        System.out.println("리스트 사이즈 ::" + list.size());


    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        preferences.clearPost(upload_sale.this, "post");
        preferences.clearUri(upload_sale.this,"uri");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}