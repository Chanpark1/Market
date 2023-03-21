package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.ReplyAdapter.reply_authNum;
import static com.chanyoung.market.signup.SERVER_ADDRESS;
import static com.chanyoung.market.signup_profile.url;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
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

public class comm_post_read extends AppCompatActivity {

    ImageView profile_image;
    TextView tv_title;
    TextView tv_category;
    TextView tv_username;
    TextView tv_area;
    TextView tv_content;
    TextView tv_like_num;
    TextView tv_reply_num;

    TextView hidden_hit;
    TextView hidden_hit_num;

    ImageButton menu;

    ImageView reply_image;
    ImageButton load_image;
    EditText input_reply;
    Button reply_submit;
    ImageButton delete_image;

    LinearLayout image_layout;
    FrameLayout image_frame;
    MultipartBody.Part multipart;
    CompoundButton like_btn;
    private ImageSliderAdapter adapter;
    private ViewPager2 viewPager2;
    private LinearLayout indicator;

    private List<multipleImage> uri_list;
    private List<comm_read_items> main_list;
    private List<ReplyItems> rep_list = new ArrayList<>();
    SharedPreferences prefs;
    boolean hasCamPerm;
    boolean hasWritePerm;

    public String authNum;
    public String isWishOrNot;
    public static boolean isBitmap = true;

    RecyclerView recyclerView;
    ReplyAdapter re_adapter;
    LinearLayoutManager manager;

    LinearLayout input_reply_layout;
    LinearLayout hidden_reply_layout;
    LinearLayout hidden_layout;
    LinearLayout hidden_image_layout;

    ImageView hidden_image_view;
    ImageButton hidden_delete_btn;
    ImageButton hidden_delete_image;
    ConstraintLayout parent;

    EditText rep_rep_input;
    Button rep_rep_submit;
    ImageButton rep_rep_load_image;

    NestedScrollView nestedScrollView;


    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_post_read);

        parent = (ConstraintLayout) findViewById(R.id.parent_constraint);

        recyclerView = (RecyclerView) findViewById(R.id.comm_read_reply_rv);

        load_image = (ImageButton) findViewById(R.id.comm_read_image_btn);
        input_reply = (EditText) findViewById(R.id.comm_read_input_reply);
        reply_submit = (Button) findViewById(R.id.comm_read_reply_submit);
        reply_image = (ImageView) findViewById(R.id.reply_image);
        delete_image = (ImageButton) findViewById(R.id.delete_reply_image);
        image_layout = (LinearLayout) findViewById(R.id.bottom_linear_image);
        image_frame = (FrameLayout) findViewById(R.id.comm_read_frame);

        nestedScrollView = (NestedScrollView) findViewById(R.id.comm_read_scroll);

        input_reply_layout = (LinearLayout) findViewById(R.id.bottom_linear);
        hidden_reply_layout = (LinearLayout) findViewById(R.id.bottom_hidden_linear);
        hidden_delete_btn = (ImageButton) findViewById(R.id.hidden_reply_delete);
        hidden_layout = (LinearLayout) findViewById(R.id.bottom_hidden_reply);
        hidden_image_layout = (LinearLayout) findViewById(R.id.bottom_hidden_linear_image);
        hidden_image_view = (ImageView) findViewById(R.id.hidden_reply_image);
        hidden_delete_image = (ImageButton) findViewById(R.id.delete_hidden_reply_image);

        rep_rep_input = (EditText) findViewById(R.id.comm_hidden_input_reply);
        rep_rep_submit = (Button) findViewById(R.id.comm_hidden_reply_submit);
        rep_rep_load_image = (ImageButton) findViewById(R.id.comm_hidden_image_btn);

        profile_image = (ImageView) findViewById(R.id.comm_read_profile);
        tv_title = (TextView) findViewById(R.id.comm_read_title);
        tv_category = (TextView) findViewById(R.id.comm_read_category);
        tv_username = (TextView) findViewById(R.id.comm_read_username);
        tv_area = (TextView) findViewById(R.id.comm_read_area);
        tv_content = (TextView) findViewById(R.id.comm_read_content);
        tv_like_num = (TextView) findViewById(R.id.comm_read_like_num);
        tv_reply_num = (TextView) findViewById(R.id.comm_read_reply_num);
        hidden_hit = (TextView) findViewById(R.id.hidden_hit);
        hidden_hit_num = (TextView) findViewById(R.id.hidden_hit_num);
        viewPager2 = (ViewPager2) findViewById(R.id.comm_read_vp);
        indicator = (LinearLayout) findViewById(R.id.comm_read_indicator);
        like_btn = (CompoundButton) findViewById(R.id.comm_read_like_btn);
        menu = (ImageButton) findViewById(R.id.comm_read_menu);

        String post_authNum = getIntent().getStringExtra("post_authNum");
        String authNums = getIntent().getStringExtra("authNum");

        hidden_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_reply_layout.setVisibility(View.VISIBLE);
                hidden_reply_layout.setVisibility(View.GONE);
                hidden_layout.setVisibility(View.GONE);
                hidden_image_layout.setVisibility(View.GONE);
                isBitmap = true;


            }
        });

        hidden_delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hidden_image_view.setImageResource(0);
                hidden_image_layout.setVisibility(View.GONE);
                isBitmap = true;

            }
        });

        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
        //TODO 첫 진입 시 로딩되는 댓글
        Call<List<ReplyItems>> rep_call = retrofit_load_post.load_reply(getAuthNum(),post_authNum);
        rep_call.enqueue(new Callback<List<ReplyItems>>() {
            @Override
            public void onResponse(Call<List<ReplyItems>> call, Response<List<ReplyItems>> response) {
                if(response.body() != null && response.isSuccessful()) {

                    rep_list = response.body();


                    for (int i = 0; i < rep_list.size(); i++) {
                        Log.d(String.valueOf(i),rep_list.get(i).getReplyIdx());
                    }
                    int count = rep_list.size() -1;
                    Log.d("최초 댓글들의 마지막 포지션", String.valueOf(count));


                    generateReplyList(rep_list);
                }
            }
            @Override
            public void onFailure(Call<List<ReplyItems>> call, Throwable t) {

            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {

//                    int count = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() + 1;

                    int count = rep_list.size() - 1;

                    if(count > 0) {
                        String reply_idx = rep_list.get(count).getReplyIdx();

                        LoadMoreReplies(getAuthNum(),post_authNum,reply_idx);
                    }

//                    Log.d("갱신 되었을 때 replyIdx : ", reply_idx);
//                    Log.d("마지막 포지션 : ", String.valueOf(count));

                }
            }
        });

        System.out.println(authNums);
        System.out.println(getAuthNum());
        if(!authNums.equals(getAuthNum())) {
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(comm_post_read.this,menu);
                    getMenuInflater().inflate(R.menu.read_menu_viewer,popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch(menuItem.getItemId()) {

                                case R.id.read_report:
                                    break;

                            }

                            return true;
                        }
                    });
                    popup.show();
                }
            });
        } else {
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(comm_post_read.this,menu);
                    getMenuInflater().inflate(R.menu.read_menu_owner,popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch(menuItem.getItemId()) {

                                case R.id.edit_post:

                                    Intent intent = new Intent(comm_post_read.this, comm_post_edit.class);
                                    intent.putExtra("post_authNum", post_authNum);
                                    intent.putExtra("authNum",authNum);
                                    intent.putExtra("category", getIntent().getStringExtra("category"));
                                    startActivity(intent);

                                case R.id.delete_post:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(comm_post_read.this);

                                    builder.setTitle("게시글 삭제").setMessage("게시글을 삭제 할까요?");

                                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
                                            Call<String> del_call = retrofit_load_post.delete_comm_post(post_authNum);
                                            del_call.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    if(response.isSuccessful() && response.body() != null)  {
                                                        Intent intent = new Intent(comm_post_read.this,MainActivity.class);

                                                        startActivity(intent);
                                                        finish();
                                                        dialogInterface.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {

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
                            }

                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }


        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(comm_post_read.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(comm_post_read.this);

                builder.setTitle("선택하세요.");

                builder.setItems(R.array.LAN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch(i) {
                            case 0 :
                                Gallery();
                                break;

                            case 1:
                                TakePhoto();
                                break;
                        }
                    }
                });
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };

                AlertDialog alertDialog = builder.create();
                builder.setNegativeButton("취소",cancel);
                alertDialog.show();

            }
        });

        rep_rep_load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(comm_post_read.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(comm_post_read.this);

                builder.setTitle("선택하세요.");

                builder.setItems(R.array.LAN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch(i) {
                            case 0 :
                                Gallery2();
                                break;

                            case 1:
                                TakePhoto2();
                                break;
                        }
                    }
                });
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };

                AlertDialog alertDialog = builder.create();
                builder.setNegativeButton("취소",cancel);
                alertDialog.show();
            }
        });

        Retrofit_basic basic = getApiClient().create(Retrofit_basic.class);

        Call<String> isWish = basic.check_comm_wish(getAuthNum(),post_authNum);
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
                Log.d("IsWishOrNot?", t.getMessage());
            }
        });

        like_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Call<String> call = basic.update_comm_like(getAuthNum(),post_authNum);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            Toast.makeText(comm_post_read.this, response.body(), Toast.LENGTH_SHORT).show();

                            if(response.body().equals("+")) {
                                String num_str = tv_like_num.getText().toString();

                                int hit_num = Integer.parseInt(num_str);

                                hit_num += 1;

                                num_str = String.valueOf(hit_num);

                                tv_like_num.setText(num_str);


                            } else {
                                String num_str = tv_like_num.getText().toString();

                                int hit_num = Integer.parseInt(num_str);

                                hit_num -= 1;

                                num_str = String.valueOf(hit_num);

                                tv_like_num.setText(num_str);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("게시글 좋아요", t.getMessage());
                    }
                });
            }
        });

        Call<List<comm_read_items>> load_post =  retrofit_load_post.load_comm_post(authNum,post_authNum);

        load_post.enqueue(new Callback<List<comm_read_items>>() {
            @Override
            public void onResponse(Call<List<comm_read_items>> call, Response<List<comm_read_items>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    main_list = response.body();

                    for(int i = 0; i < main_list.size(); i++) {

                        String username = main_list.get(i).getUsername();
                        String title = main_list.get(i).getTitle();
                        String content = main_list.get(i).getContent();
                        String area = main_list.get(i).getArea();
                        String hit_num = main_list.get(i).getHit_num();
                        String like_num = main_list.get(i).getLike_num();
                        String reply_num = main_list.get(i).getReply_num();
                        String category = main_list.get(i).getCategory();
                        String path = main_list.get(i).getPath();

                        if(path == null) {
                            Glide.with(comm_post_read.this)
                                    .load(url)
                                    .into(profile_image);
                        } else {
                            Glide.with(comm_post_read.this)
                                    .load(path)
                                    .into(profile_image);
                        }

                        tv_username.setText(username);
                        tv_title.setText(title);
                        tv_area.setText(area);
                        tv_category.setText(category);
                        tv_content.setText(content);
                        tv_like_num.setText(like_num);
                        tv_reply_num.setText(reply_num);
                        hidden_hit_num.setText(hit_num);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<comm_read_items>> call, Throwable t) {
                Log.d(TAG,t.getMessage());

            }
        });

        Call<List<multipleImage>> req_image = retrofit_load_post.request_comm_image(post_authNum);
        req_image.enqueue(new Callback<List<multipleImage>>() {
            @Override
            public void onResponse(Call<List<multipleImage>> call, Response<List<multipleImage>> response) {
                if(response.body() != null && response.isSuccessful()) {
                    uri_list = response.body();
                    image_frame.setVisibility(View.VISIBLE);

                    viewPager2.setOffscreenPageLimit(1);
                    adapter = new ImageSliderAdapter(comm_post_read.this,uri_list);
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
                Log.d(TAG, t.getMessage());
            }
        });
        reply_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = input_reply.getText().toString();


                if(!isBitmap) {

                    Bitmap bitmap = ((BitmapDrawable)reply_image.getDrawable()).getBitmap();

                    Uri uri = getUri(comm_post_read.this,bitmap);

                    String path = getAbsolutePath(uri);

                    File file = BitmapToFile(bitmap,path);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"),file);
                    multipart = MultipartBody.Part.createFormData("uploaded_file",file.getName(),requestBody);
                }

                HashMap<String,RequestBody> hashMap = new HashMap<>();

                RequestBody content_body = RequestBody.create(MediaType.parse("text/plain"),content);
                RequestBody auth_body = RequestBody.create(MediaType.parse("text/plain"),getAuthNum());
                RequestBody post_body = RequestBody.create(MediaType.parse("text/plain"),post_authNum);
                hashMap.put("content",content_body);
                hashMap.put("authNum",auth_body);
                hashMap.put("post_authNum",post_body);

                Call<List<ReplyItems>> call = retrofit_load_post.upload_reply(multipart,hashMap);
                call.enqueue(new Callback<List<ReplyItems>>() {
                    @Override
                    public void onResponse(Call<List<ReplyItems>> call, Response<List<ReplyItems>> response) {
                        if(response.isSuccessful() && response.body() != null) {

                            for(int i = 0; i < response.body().size(); i++) {
                                String content = response.body().get(i).getContent();
                                String authNum = response.body().get(i).getAuthNum();
                                String replyIdx = response.body().get(i).getReplyIdx();
                                String post_authNum = response.body().get(i).getPost_authNum();
                                String reply_authNum = response.body().get(i).getReply_authNum();
                                String like_num = response.body().get(i).getLike_num();
                                String user_path = response.body().get(i).getUser_path();
                                String image_path = response.body().get(i).getImage_path();
                                String area = response.body().get(i).getArea();
                                String username = response.body().get(i).getUsername();
                                String created = response.body().get(i).getCreated();

                                rep_list.add(new ReplyItems(content,replyIdx, authNum,post_authNum,reply_authNum,like_num,user_path,image_path,area,username,created));

                                recyclerView = (RecyclerView) findViewById(R.id.comm_read_reply_rv);

                                re_adapter = new ReplyAdapter(comm_post_read.this,rep_list);
                                manager = new LinearLayoutManager(comm_post_read.this);
                                manager.setReverseLayout(true);
                                manager.setStackFromEnd(true);

                                recyclerView.setAdapter(re_adapter);
                                recyclerView.setLayoutManager(manager);
                                re_adapter.notifyDataSetChanged();
                                input_reply.setText("");

                                String num = tv_reply_num.getText().toString();
                                int nus = Integer.parseInt(num);
                                nus += 1;
                                tv_reply_num.setText(String.valueOf(nus));
                                reply_image.setImageResource(0);
                                image_layout.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReplyItems>> call, Throwable t) {

                    }
                });

            }
        });

        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply_image.setImageResource(0);
                image_layout.setVisibility(View.GONE);
            }
        });


    }

    private void LoadMoreReplies(String authNum, String post_authNum, String count) {
        recyclerView = (RecyclerView) findViewById(R.id.comm_read_reply_rv);

        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);

        Call<List<ReplyItems>> call = retrofit_load_post.load_more_reply(authNum,post_authNum,count);
        call.enqueue(new Callback<List<ReplyItems>>() {
            @Override
            public void onResponse(Call<List<ReplyItems>> call, Response<List<ReplyItems>> response) {
                if(response.isSuccessful() && response.body() != null) {

//                    generateReplyList(response.body());

                    for(int i = 0; i < response.body().size(); i++) {
                        String replyIdx = response.body().get(i).getReplyIdx();
                        String content = response.body().get(i).getContent();
                        String area = response.body().get(i).getArea();
                        String username = response.body().get(i).getUsername();
                        String like_num = response.body().get(i).getLike_num();
                        String reply_authNum = response.body().get(i).getReply_authNum();
                        String authNum = response.body().get(i).getAuthNum();
                        String post_authNum = response.body().get(i).getPost_authNum();
                        String created = response.body().get(i).getCreated();
                        String image_path = response.body().get(i).getImage_path();
                        String user_path = response.body().get(i).getUser_path();

                        re_adapter.addItem(new ReplyItems(content,replyIdx,authNum,post_authNum,reply_authNum,like_num,user_path,image_path,area,username,created));


                    }
                    re_adapter.notifyDataSetChanged();
                    Log.d("갱신 되었을 때 리스트 사이즈 : ", String.valueOf(rep_list.size()));


                }
            }

            @Override
            public void onFailure(Call<List<ReplyItems>> call, Throwable t) {
                Log.d("LoadMoreReplies",t.getMessage());
            }
        });

    }


    public void generateReplyList(List<ReplyItems> list) {
        recyclerView = (RecyclerView) findViewById(R.id.comm_read_reply_rv);

        re_adapter = new ReplyAdapter(comm_post_read.this,list);
        manager = new LinearLayoutManager(comm_post_read.this);
        manager.setStackFromEnd(true);

        recyclerView.setAdapter(re_adapter);
        recyclerView.setLayoutManager(manager);

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

    private File BitmapToFile(Bitmap bitmap, String path) {
        File file = new File(path);

        OutputStream stream = null;

        try {
            file.createNewFile();
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return file;
    }

    public void Gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imageLauncher.launch(intent);
        isBitmap = false;
    }


    public void TakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        isBitmap = false;
        startActivityForResult(intent,222);

    }


    public void Gallery2() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imageLauncher2.launch(intent);
        isBitmap = false;
    }


    public void TakePhoto2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        isBitmap = false;
        startActivityForResult(intent,333);

    }

    private Uri getUri(Context context, Bitmap bitmap) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"IMG_"+ Calendar.getInstance().getTime(),null);

        return Uri.parse(path);
    }
    ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();

                        Log.e(TAG, "URI => " + uri.getPath());

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            image_layout.setVisibility(View.VISIBLE);
                            reply_image.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
    );

    ActivityResultLauncher<Intent> imageLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            hidden_image_layout.setVisibility(View.VISIBLE);
                            hidden_image_view.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 222 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();


            Bitmap image = (Bitmap) extras.get("data");


            Uri uri = getUri(getApplicationContext(),image);
            Log.e(TAG, "URI => " + uri);

            image_layout.setVisibility(View.VISIBLE);
            reply_image.setImageBitmap(image);
        } else if(requestCode == 333 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            Bitmap image =(Bitmap)  extras.get("data");

            Uri uri = getUri(getApplicationContext(), image);

            hidden_image_layout.setVisibility(View.VISIBLE);
            hidden_image_view.setImageBitmap(image);
        }
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


    public String getAuthNum() {
        prefs = getSharedPreferences("Username",MODE_PRIVATE);

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

    private HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(comm_post_read.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}