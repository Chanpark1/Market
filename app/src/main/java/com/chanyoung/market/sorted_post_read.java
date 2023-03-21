package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.signup.SERVER_ADDRESS;
import static com.chanyoung.market.signup_profile.url;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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

public class sorted_post_read extends AppCompatActivity implements OnMapReadyCallback {

    private ViewPager2 viewPager2;
    private LinearLayout indicator;

    private List<multipleImage> uri_list;
    private List<read_postItem> main_list;
    private List<Coordinates> co_list;

    private ImageSliderAdapter adapter;

    public MapView mapView;
    public NaverMap naverMap;

    private double latitude;
    private double longitude;

    public String category;

    CompoundButton like_btn;
    ImageView profile_image;
    TextView profile_name;
    TextView profile_address;
    TextView tv_title;
    TextView tv_description;
    TextView tv_price;
    TextView tv_like_num;
    TextView tv_chat_num;
    TextView tv_hit_num;
    TextView tv_category;
    TextView sale_read_logo;
    TextView tv_time;

    LinearLayout linearMap;

    public String user_authNum;

    Spinner spinner;
    ArrayAdapter<CharSequence> sp_adapter;

    ImageButton exit;

    Button chat;

    ImageButton menu;

    SharedPreferences prefs;

    FragmentManager fm = getSupportFragmentManager();

    public String status;
    public String isWishOrNot;

    MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.read_map_view);
    BounceInterpolator bounceInterpolator;
    ScaleAnimation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_sale_read);
        mapView = (MapView) findViewById(R.id.read_map_view);
        sale_read_logo = (TextView) findViewById(R.id.sale_read_logo);
        linearMap = (LinearLayout) findViewById(R.id.linear);
        like_btn = (CompoundButton) findViewById(R.id.sale_read_like_btn);

        Intent getIntent = getIntent();

        String getPost_authNum = getIntent.getStringExtra("post_authNum");

        spinner = (Spinner) findViewById(R.id.sale_read_spinner);


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

        Retrofit_load_post req_co = retrofit.create(Retrofit_load_post.class);
        Call<List<Coordinates>> request = req_co.request_co(getPost_authNum);
        request.enqueue(new Callback<List<Coordinates>>() {
            @Override
            public void onResponse(Call<List<Coordinates>> call, Response<List<Coordinates>> response) {
                if(response.isSuccessful() && response.body() != null) {

                    co_list = response.body();

                    for(int i = 0; i < co_list.size(); i++) {
                        String lat = co_list.get(i).getTrade_lat();
                        String lng = co_list.get(i).getTrade_lng();
                        user_authNum = co_list.get(i).getAuthNum();
                        latitude = Double.parseDouble(lat);
                        longitude = Double.parseDouble(lng);
                        if(latitude != 0 && longitude != 0) {

                            if(mapFragment == null) {
                                mapFragment = MapFragment.newInstance();
                                fm.beginTransaction().add(R.id.read_map_view,mapFragment).commit();
                                mapFragment.getMapAsync(sorted_post_read.this);
                                sale_read_logo.setVisibility(View.VISIBLE);
                                linearMap.setVisibility(View.VISIBLE);
                            }


                        } else {
                            sale_read_logo.setVisibility(View.GONE);
                            linearMap.setVisibility(View.GONE);

                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<List<Coordinates>> call, Throwable t) {
                Log.d(TAG,t.getMessage());
            }
        });

        viewPager2 = (ViewPager2) findViewById(R.id.sale_read_viewpager);
        indicator = (LinearLayout) findViewById(R.id.layout_indicator);


        profile_image = (ImageView) findViewById(R.id.sale_read_profile_image);
        profile_name = (TextView) findViewById(R.id.sale_read_name);
        profile_address = (TextView) findViewById(R.id.sale_read_address);
        tv_title = (TextView) findViewById(R.id.sale_read_title);
        tv_description = (TextView) findViewById(R.id.sale_read_description);
        tv_price = (TextView) findViewById(R.id.sale_read_price);
        tv_like_num = (TextView) findViewById(R.id.sale_read_like_num);
        tv_chat_num = (TextView) findViewById(R.id.sale_read_chat_num);
        tv_hit_num = (TextView) findViewById(R.id.sale_read_hit_num);
        tv_category = (TextView) findViewById(R.id.sale_read_category);
        tv_time = (TextView) findViewById(R.id.sale_read_date);
        menu = (ImageButton) findViewById(R.id.read_menu);

        chat = (Button) findViewById(R.id.sale_read_chat_btn);

        prefs = getSharedPreferences("Username",MODE_PRIVATE);
        Retrofit_basic retrofit_basic = retrofit.create(Retrofit_basic.class);
        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();


        String value = (String) it_val.next();

            try {
                JSONObject jsonObject = new JSONObject(value);

                String authNum = jsonObject.getString("authNum");

                Call<String> isWish = retrofit_basic.check_wish(authNum,getPost_authNum);

                isWish.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            isWishOrNot = response.body();

                            if(isWishOrNot.equals("O")) {
                                like_btn.setButtonDrawable(R.drawable.button_selected_true);
                            } else {
                                like_btn.setButtonDrawable(R.drawable.button_selected_false);
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

                like_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        compoundButton.setAnimation(scaleAnimation);

                        Call<String> update_like = retrofit_basic.update_like(authNum,getPost_authNum);
                        update_like.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(sorted_post_read.this, response.body(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
                            }
                        });
                    }
                });

                String auths = getIntent.getStringExtra("authNum");

                if(!authNum.equals(auths) ) {

                    menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(sorted_post_read.this,menu);
                            getMenuInflater().inflate(R.menu.read_menu_viewer,popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    switch (menuItem.getItemId()) {

                                        case R.id.read_report :
                                            break;
                                    }
                                    return true;
                                }
                            });
                            popup.show();

                        }
                    });

                } else {

                    spinner.setVisibility(View.VISIBLE);
                    sp_adapter = ArrayAdapter.createFromResource(this, R.array.post_status, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                    spinner.setAdapter(sp_adapter);

                    String st = getIntent.getStringExtra("status");
                    if(st.equals("판매중")) {
                        spinner.setSelection(0);
                    } else if(st.equals("예약중")) {
                        spinner.setSelection(1);
                    } else if(st.equals("판매완료")) {
                        spinner.setSelection(2);
                    }


                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            status = spinner.getSelectedItem().toString();

                            Retrofit_basic retrofit_basic = retrofit.create(Retrofit_basic.class);
                            Call<String> up_stat = retrofit_basic.update_status(status,getPost_authNum);
                            up_stat.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(response.isSuccessful() && response.body() != null) {
                                        Log.d("결과는?", response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d(TAG, t.getMessage());
                                }
                            });

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {




                        }
                    });


                    menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(sorted_post_read.this,menu);
                            getMenuInflater().inflate(R.menu.read_menu_owner,popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    switch (menuItem.getItemId()) {

                                        case R.id.edit_post :

                                            Intent intent = new Intent(sorted_post_read.this,edit_sale_post.class);

                                            String title = tv_title.getText().toString();
                                            String price  = tv_price.getText().toString();
                                            String description = tv_description.getText().toString();

                                            String tr_lat = String.valueOf(latitude);
                                            String tr_lng = String.valueOf(longitude);


                                            intent.putExtra("title",title);
                                            intent.putExtra("price",price);
                                            intent.putExtra("description",description);
                                            intent.putExtra("trade_lat",tr_lat);
                                            intent.putExtra("trade_lng",tr_lng);
                                            intent.putExtra("post_authNum",getPost_authNum);
                                            startActivity(intent);
                                            finish();
                                            Log.d("trade_lat : ", tr_lat);
                                            Log.d("trade_lng : ", tr_lng);

                                        case R.id.delete_post :

                                            AlertDialog.Builder builder = new AlertDialog.Builder(sorted_post_read.this);

                                            builder.setTitle("게시글 삭제").setMessage("게시글을 삭제 할까요?");

                                            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    Retrofit_load_post req_delete = retrofit.create(Retrofit_load_post.class);
                                                    Call<String> del_call = req_delete.delete_sale_post(getPost_authNum);

                                                    del_call.enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {
                                                            if(response.isSuccessful() && response.body() != null) {
                                                                Intent intent = new Intent(sorted_post_read.this,MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                                dialogInterface.dismiss();

                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {
                                                            Log.d("게시글 삭제 Fail", t.getMessage());
                                                        }
                                                    });

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
//                                            dialog.dismiss();

                                    }

                                    return true;
                                }
                            });
                            popup.show();
//                            popup.dismiss();
                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }




        Retrofit_load_post retrofit_load_post = retrofit.create(Retrofit_load_post.class);

        Call<List<read_postItem>> call = retrofit_load_post.read_post(getPost_authNum);

        call.enqueue(new Callback<List<read_postItem>>() {
            @Override
            public void onResponse(Call<List<read_postItem>> call, Response<List<read_postItem>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    main_list = response.body();

                    for(int i = 0; i < main_list.size(); i++) {
                        String title = main_list.get(i).getTitle();
                        String price = main_list.get(i).getPrice();
                        String description = main_list.get(i).getDescription();
                        String area = main_list.get(i).getArea();
                        String like_num = main_list.get(i).getLike_num();
                        String chat_num = main_list.get(i).getChat_num();
                        String profile_image_uri = main_list.get(i).getProfile_image();
                        String username = main_list.get(i).getUsername();
                        category = main_list.get(i).getCategory();
                        String hit_num = main_list.get(i).getHit_num();
                        String date = main_list.get(i).getCreated();

                        if(profile_image_uri == null) {
                            Glide.with(sorted_post_read.this)
                                    .load(url)
                                    .into(profile_image);
                        } else {
                            Glide.with(sorted_post_read.this)
                                    .load(profile_image_uri)
                                    .into(profile_image);
                        }


                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

                        try {
                            Date mDate = format.parse(date);

                            String stDate = formatTimeString(mDate);
                            tv_time.setText(stDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        profile_name.setText(username);
                        tv_title.setText(title);
                        tv_price.setText(price+"원");
                        tv_description.setText(description);
                        profile_address.setText(area);
                        tv_like_num.setText(like_num);
                        tv_chat_num.setText(chat_num);
                        tv_category.setText(category);
                        tv_hit_num.setText(hit_num);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<read_postItem>> call, Throwable t) {
                Log.d(TAG,t.getMessage());
            }
        });

        Call<List<multipleImage>> req_image = retrofit_load_post.request_image(getPost_authNum);
        req_image.enqueue(new Callback<List<multipleImage>>() {
            @Override
            public void onResponse(Call<List<multipleImage>> call, Response<List<multipleImage>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    uri_list = response.body();


                    viewPager2.setOffscreenPageLimit(1);

                    adapter = new ImageSliderAdapter(sorted_post_read.this,uri_list);

                    viewPager2.setAdapter(adapter);

                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            setCurrentIndicator(position);

                        }
                    });
                    setupIndicators(uri_list.size());

                }
            }

            @Override
            public void onFailure(Call<List<multipleImage>> call, Throwable t) {

            }
        });


        exit = (ImageButton) findViewById(R.id.read_sale_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sorted_post_read.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(16,8,16,8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            indicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }



    private void setCurrentIndicator(int position) {
        int childCount = indicator.getChildCount();

        for(int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicator.getChildAt(i);

            if(i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
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

        LatLng latLng = new LatLng(latitude,longitude);
        CameraPosition camera = new CameraPosition(latLng,17);
        naverMap.setCameraPosition(camera);
        Marker marker = new Marker();
        setMark(marker,latitude,longitude,R.drawable.ic_baseline_location_on_24);

        Log.d("Camera Position", String.valueOf(latitude));
        Log.d("Camera Position", String.valueOf(longitude));

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(sorted_post_read.this,Sort_by_category.class);
        intent.putExtra("category",category);
        startActivity(intent);
        finish();

    }


    public String formatTimeString(Date mDate) {
        long curTime = System.currentTimeMillis();

        long reqTime = mDate.getTime();

        long diffTime = (curTime - reqTime) / 1000;
        String msg = null;


        if (diffTime < postAdapter.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.SEC) < postAdapter.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.MIN) < postAdapter.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.HOUR) < postAdapter.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.DAY) < postAdapter.TIME_MAXIMUM.MONTH ) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

    }
