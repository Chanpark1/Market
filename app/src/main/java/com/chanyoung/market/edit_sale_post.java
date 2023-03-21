package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.signup_location.SERVER_ADDRESS;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

public class edit_sale_post extends AppCompatActivity implements OnMapReadyCallback {

    Button upload;

    ImageButton exit;
    ImageButton imageButton;
    ImageButton del_location;

    Spinner spinner = null;
    ArrayAdapter<CharSequence> sp_adapter = null;

    EditText input_title;
    EditText input_price;
    EditText input_description;

    Button trade;


    SharedPreferences prefs;

    boolean hasCamPerm;
    boolean hasWritePerm;
    List<Uri> list = new ArrayList<>();
    List<multipleImage> multiple_list;

    List<Uri> added_list = new ArrayList<>();

    List<Uri> deleted_list = new ArrayList<>();
    List<String> deleted_list_toString = new ArrayList<>();

    List<Uri> origin_list = new ArrayList<>();


    List<read_postItem> item_list = new ArrayList<>();


    RecyclerView recyclerView;
    imageAdapter_edit adapter;
    LinearLayoutManager manager;

    TextView image_num;

    public String trade_lat;
    public String trade_lng;

    public String prefs_title;
    public String prefs_price;
    public String prefs_content;

    public String called_title;
    public String called_price;
    public String called_content;
    public String called_cat;

    public String category;

    public JSONArray jsonArray;

    public MapView mapView;
    public NaverMap naverMap;

    public double lat;
    public double lng;

    public String post_authNum;

    SavedSharedPreferences preferences = new SavedSharedPreferences();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sale_post);
        mapView = (MapView) findViewById(R.id.edit_sale_map_view);
        mapView.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        post_authNum = getIntent().getStringExtra("post_authNum");

        del_location = (ImageButton) findViewById(R.id.edit_sale_del_location);
        input_title = (EditText) findViewById(R.id.edit_sale_title);
        input_price = (EditText) findViewById(R.id.edit_sale_price);
        input_description = (EditText) findViewById(R.id.edit_sale_description);
        upload = (Button) findViewById(R.id.edit_button);
        imageButton = (ImageButton) findViewById(R.id.edit_sale_image);
        image_num = (TextView) findViewById(R.id.edit_image_num);
        spinner = (Spinner) findViewById(R.id.edit_sale_category);
        sp_adapter = ArrayAdapter.createFromResource(this, R.array.category, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(sp_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        trade = (Button) findViewById(R.id.edit_sale_location);

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

//            Retrofit_load_post req_co = retrofit.create(Retrofit_load_post.class);
//            Call<List<Coordinates>> request = req_co.request_co(post_authNum);
//
//           //여기서 최초로 서버에서 좌표를 받아서 지도를 띄워줌.
//
//            request.enqueue(new Callback<List<Coordinates>>() {
//                @Override
//                public void onResponse(Call<List<Coordinates>> call, Response<List<Coordinates>> response) {
//                    if(response.isSuccessful() && response.body() != null) {
//                        co_list = response.body();
//
//                        for (int i = 0; i < co_list.size(); i++) {
//
//                            trade_lat = co_list.get(i).getTrade_lat();
//                            trade_lng = co_list.get(i).getTrade_lng();
//
//                            lat = Double.parseDouble(trade_lat);
//                            lng = Double.parseDouble(trade_lng);
//
//                            mapView.setVisibility(View.VISIBLE);
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Coordinates>> call, Throwable t) {
//
//                }
//            });


        Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);
        Call<List<read_postItem>> load_items  = retrofit_load_post.read_post(post_authNum);

        load_items.enqueue(new Callback<List<read_postItem>>() {
            @Override
            public void onResponse(Call<List<read_postItem>> call, Response<List<read_postItem>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    item_list = response.body();

                    for(int i = 0; i < item_list.size(); i++) {

                        called_title = item_list.get(i).getTitle();
                        called_price = item_list.get(i).getPrice();
                        called_content = item_list.get(i).getDescription();
                        called_cat = item_list.get(i).getCategory();

                        input_title.setText(called_title);
                        input_price.setText(called_price);
                        input_description.setText(called_content);



                    }
                }
            }

            @Override
            public void onFailure(Call<List<read_postItem>> call, Throwable t) {

            }
        });

        if(extras != null) {

            trade_lat = getIntent().getStringExtra("trade_lat");
            trade_lng = getIntent().getStringExtra("trade_lng");

            lat = Double.parseDouble(trade_lat);
            lng = Double.parseDouble(trade_lng);

            if(lat == 0 && lng == 0) {
                mapView.setVisibility(View.GONE);
            } else {
                mapView.setVisibility(View.VISIBLE);
                del_location.setVisibility(View.VISIBLE);
            }

            del_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trade_lat = "0";
                    trade_lng = "0";

                    mapView.setVisibility(View.GONE);
                    del_location.setVisibility(View.GONE);
                }
            });


        }

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit_sale_post.this, trade_location_edit.class);
                intent.putExtra("post_authNum",post_authNum);
                intent.putExtra("authNum",getIntent().getStringExtra("authNum"));
                intent.putExtra("status",getIntent().getStringExtra("status"));

                startActivity(intent);


            }
        });

        hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        recyclerView = (RecyclerView) findViewById(R.id.edit_sale_rv);
        adapter = new imageAdapter_edit(edit_sale_post.this,list,deleted_list,added_list);
        manager = new LinearLayoutManager(edit_sale_post.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        Call<List<multipleImage>> req_image = retrofit_load_post.request_image(post_authNum);
        req_image.enqueue(new Callback<List<multipleImage>>() {
            @Override
            public void onResponse(Call<List<multipleImage>> call, Response<List<multipleImage>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    multiple_list = response.body();

                    for (int i = 0; i < multiple_list.size(); i++) {
                        String uri_string = multiple_list.get(i).getPath();
                        Uri uri = Uri.parse(uri_string);
                        list.add(uri);
                        //TODO list = RecyclerView 출력 용도

                        deleted_list.add(uri);
                        //TODO 삭제된 파일을 비교해서 unlink 하기 위한 list. 이미지 삭제 시 같이 적용한다.

                        origin_list.add(uri);
                        //TODO deleted_list 와 서버에서 array_diff() 하기 위한 원본 이미지 배열


                        generateDataList(list);

                        Log.d("rv_list : ", String.valueOf(list.toString()));
                        Log.d("deleted_list : ", String.valueOf(deleted_list.toString()));

                    }
                    image_num.setText(multiple_list.size() + " / 10");
                }
            }

            @Override
            public void onFailure(Call<List<multipleImage>> call, Throwable t) {
                Log.d(TAG,t.getMessage());
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(edit_sale_post.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(edit_sale_post.this);

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

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String title = input_title.getText().toString();
            String price = input_price.getText().toString();
            String description = input_description.getText().toString();

            RequestBody title_body = RequestBody.create(MediaType.parse("text/plain"),title);
            RequestBody price_body = RequestBody.create(MediaType.parse("text/plain"),price);
            RequestBody des_body = RequestBody.create(MediaType.parse("text/plain"),description);
            RequestBody lat_body = RequestBody.create(MediaType.parse("text/plain"),trade_lat);
            RequestBody lng_body = RequestBody.create(MediaType.parse("text/plain"),trade_lng);
            RequestBody post_auth_body = RequestBody.create(MediaType.parse("text/plain"),post_authNum);
            RequestBody cat_body = RequestBody.create(MediaType.parse("text/plain"),category);

            HashMap<String,RequestBody> hashMap = new HashMap<>();
                hashMap.put("title",title_body);
                hashMap.put("price",price_body);
                hashMap.put("description",des_body);
                hashMap.put("post_authNum",post_auth_body);
                hashMap.put("trade_lat",lat_body);
                hashMap.put("trade_lng",lng_body);
                hashMap.put("category",cat_body);

                prefs = getSharedPreferences("Username",MODE_PRIVATE);

                Collection<?> col_val = prefs.getAll().values();
                Iterator<?> it_val = col_val.iterator();

                while(it_val.hasNext()) {
                    String value = (String) it_val.next();

                    try {
                        JSONObject jsonObject = new JSONObject(value);

                        String authNum = jsonObject.getString("authNum");
                        RequestBody auth_body = RequestBody.create(MediaType.parse("text/plain"), authNum);
                        hashMap.put("authNum",auth_body);

                    } catch (JSONException e){
                         e.printStackTrace();
                    }
                }


                ArrayList<MultipartBody.Part> added_files = new ArrayList<>();

                for (int i = 0; i < added_list.size(); i++) {
                    Uri added_uri = added_list.get(i);

                    String added_path = getAbsolutePath(added_uri);

                    File added_file = new File(added_path);

                    RequestBody added_fileBody = RequestBody.create(MediaType.parse("image/jpeg"), added_file);

                    String fileName = "photo" + i + ".jpg";

                    MultipartBody.Part added_filePart = MultipartBody.Part.createFormData("added_file"+i, fileName, added_fileBody);

                    added_files.add(added_filePart);


                }
                String added_size = String.valueOf(added_files.size());
                RequestBody added_files_size = RequestBody.create(MediaType.parse("text/plain"),added_size);

                String deleted_size = String.valueOf(deleted_list.size());
                RequestBody deleted_files_size = RequestBody.create(MediaType.parse("text/plain"),deleted_size);

                for(int k = 0; k < deleted_list.size(); k++) {

                    Uri path = deleted_list.get(k);

                    String path_toString = path.toString();

                    deleted_list_toString.add(path_toString);


                }
                RequestBody deleted_files = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(deleted_list));


                hashMap.put("deleted_list",deleted_files);
                hashMap.put("added_count", added_files_size);
                hashMap.put("deleted_count",deleted_files_size);


                Retrofit_edit_post retrofit_edit_post = retrofit.create(Retrofit_edit_post.class);
                Call<String> upload_edit = retrofit_edit_post.editPost(added_files,hashMap);
                upload_edit.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
//                            Log.d("이미지 수정 ", response.body());

                            Intent intent = new Intent(edit_sale_post.this,upload_sale_read.class);
                            intent.putExtra("post_authNum",post_authNum);
                            intent.putExtra("authNum",getIntent().getStringExtra("authNum"));
                            intent.putExtra("status",getIntent().getStringExtra("status"));
                            startActivity(intent);
                            finish();

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("이미지 수정 FAIL", t.getMessage());
                    }
                });

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

    private void generateDataList(List<Uri> list) {
        recyclerView = findViewById(R.id.edit_sale_rv);

        adapter = new imageAdapter_edit(edit_sale_post.this,list,deleted_list,added_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(edit_sale_post.this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
                    added_list.add(imageUri);
                    //TODO 사진 추가시 리사이클러뷰용 list 와 서버에 추가될 새로운 이미지를 added_list 에 담아준다.
                } else {
                    ClipData clipData = data.getClipData();
                    if(clipData.getItemCount() > 10) {
                        Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        for(int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();

                            try {
                                list.add(imageUri);
                                added_list.add(imageUri);

                                Log.d("added_list 추가됨 ", String.valueOf(added_list.size()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        adapter = new imageAdapter_edit(edit_sale_post.this,list,deleted_list,added_list);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(manager);

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
    protected void onPause() {
        super.onPause();

        prefs_title = input_title.getText().toString();
        prefs_price = input_price.getText().toString();
        prefs_content = input_description.getText().toString();

        String save_form = "{\"title\":\""+prefs_title+"\",\"price\":\""+prefs_price+"\",\"content\":\""+prefs_content+"\"}";

        preferences.clearEdit(edit_sale_post.this, "edit");
        preferences.setEdit(edit_sale_post.this,"edit",save_form);

       jsonArray = new JSONArray();

        for(int i = 0; i < list.size(); i++) {
            jsonArray.put(list.get(i));
        }
        preferences.clearUri(edit_sale_post.this, "uri");
        preferences.setUri(edit_sale_post.this, "uri",jsonArray);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("edit",MODE_PRIVATE);

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.clearUri(edit_sale_post.this,"uri");
        Log.d("onDestroy","Called");
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Marker marker = new Marker();

            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(lat,lng), 15
            );

            naverMap.setCameraPosition(cameraPosition);


            setMark(marker, lat, lng, R.drawable.ic_baseline_location_on_24);

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
}