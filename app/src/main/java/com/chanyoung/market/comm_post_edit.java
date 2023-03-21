package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.annotation.Nullable;
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
import android.database.Cursor;
import android.net.Uri;
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

public class comm_post_edit extends AppCompatActivity {

    Spinner spinner = null;
    ArrayAdapter<CharSequence> sp_adapter = null;

    EditText input_title;
    EditText input_content;
    TextView image_num;

    Button submit;

    ImageButton imageButton;
    RecyclerView rv;
    imageAdapter_edit_comm adapter;
    LinearLayoutManager manager;

    boolean hasCamPerm;
    boolean hasWritePerm;

    SharedPreferences prefs;

    List<Uri> list = new ArrayList<>();
    List<Uri> deleted_list = new ArrayList<>();
    List<Uri> added_list = new ArrayList<>();
    List<Uri> origin_list = new ArrayList<>();
    List<multipleImage> multiple_list = new ArrayList<>();
    List<String> deleted_list_toString = new ArrayList<>();
    List<comm_read_items> item_list = new ArrayList<>();

    public String authNum;
    public String post_authNum;
    public String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_post_edit);

        input_title = (EditText) findViewById(R.id.comm_edit_title);
        input_content = (EditText) findViewById(R.id.comm_edit_content);
        image_num = (TextView) findViewById(R.id.comm_edit_image_num);
        submit = (Button) findViewById(R.id.comm_edit_submit);
        imageButton = (ImageButton) findViewById(R.id.comm_edit_image_btn);

        spinner = (Spinner) findViewById(R.id.comm_edit_spinner);
        sp_adapter = ArrayAdapter.createFromResource(this,R.array.comm_category, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
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


        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
        Call<List<comm_read_items>> call_items = retrofit_load_post.load_edit_post(getAuthNum(),getIntent().getStringExtra("post_authNum"));
        call_items.enqueue(new Callback<List<comm_read_items>>() {
            @Override
            public void onResponse(Call<List<comm_read_items>> call, Response<List<comm_read_items>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    item_list = response.body();

                    for (int i = 0; i < item_list.size(); i++) {
                        String title = item_list.get(i).getTitle();
                        String content = item_list.get(i).getContent();


                        input_title.setText(title);
                        input_content.setText(content);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<comm_read_items>> call, Throwable t) {

            }
        });

        post_authNum = getIntent().getStringExtra("post_authNum");

        rv = (RecyclerView) findViewById(R.id.comm_edit_rv);
        adapter = new imageAdapter_edit_comm(comm_post_edit.this,list,deleted_list,added_list);
        manager = new LinearLayoutManager(comm_post_edit.this,LinearLayoutManager.HORIZONTAL, false);
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);


        Call<List<multipleImage>> req_image = retrofit_load_post.request_comm_image(post_authNum);
        req_image.enqueue(new Callback<List<multipleImage>>() {
            @Override
            public void onResponse(Call<List<multipleImage>> call, Response<List<multipleImage>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    multiple_list = response.body();

                    for (int i = 0; i < multiple_list.size(); i++) {
                        String uri_str = multiple_list.get(i).getPath();
                        Uri uri = Uri.parse(uri_str);
                        list.add(uri);
                        //TODO list : RecyclerView 출력용도

                        deleted_list.add(uri);
                        //TODO 삭제된 파일을 비교해서 unlink 하기 위한 list. 이미지 삭제 시 같이 적용한다.

                        origin_list.add(uri);
                        //TODO deleted_list 와 서버에서 array_diff() 하기 위한 원본 이미지 배열

                        generateDataList(list);
                        image_num.setText(list.size() + " / 10");
                    }

                }
            }

            @Override
            public void onFailure(Call<List<multipleImage>> call, Throwable t) {

            }
        });


        String st = getIntent().getStringExtra("category");
        if(st.equals("동네질문")) {
            spinner.setSelection(0);
        } else if(st.equals("동네사건사고")) {
            spinner.setSelection(1);
        } else if(st.equals("동네맛집")) {
            spinner.setSelection(2);
        } else if(st.equals("취미생활")) {
            spinner.setSelection(3);
        } else if(st.equals("일상")) {
            spinner.setSelection(4);
        } else if(st.equals("분실/실종센터")) {
            spinner.setSelection(5);
        } else if(st.equals("해주세요")) {
            spinner.setSelection(6);
        } else if(st.equals("동네사진전")) {
            spinner.setSelection(7);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(comm_post_edit.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(comm_post_edit.this);

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = input_title.getText().toString();
                String content = input_content.getText().toString();

                RequestBody title_body = RequestBody.create(MediaType.parse("text/plain"),title);
                RequestBody post_auth_body = RequestBody.create(MediaType.parse("text/plain"),post_authNum);
                RequestBody cat_body = RequestBody.create(MediaType.parse("text/plain"),category);
                RequestBody auth_body = RequestBody.create(MediaType.parse("text/plain"),getAuthNum());
                RequestBody content_body = RequestBody.create(MediaType.parse("text/plain"),content);

                HashMap<String, RequestBody> hashMap = new HashMap<>();
                hashMap.put("title",title_body);
                hashMap.put("post_authNum",post_auth_body);
                hashMap.put("category",cat_body);
                hashMap.put("authNum",auth_body);
                hashMap.put("content",content_body);

                ArrayList<MultipartBody.Part> added_files = new ArrayList<>();

                for(int i = 0; i < added_list.size(); i++) {
                    Uri added_uri = added_list.get(i);

                    String added_path = getAbsolutePath(added_uri);

                    File added_file = new File(added_path);

                    RequestBody added_fileBody = RequestBody.create(MediaType.parse("image/jpeg"),added_file);

                    String fileName = "photo" + i + ".jpg";

                    MultipartBody.Part added_filePart = MultipartBody.Part.createFormData("added_file"+i, fileName, added_fileBody);

                    added_files.add(added_filePart);

                }

                for(int j = 0; j < deleted_list.size(); j++) {

                    Uri path = deleted_list.get(j);

                    String path_toString = path.toString();

                    deleted_list_toString.add(path_toString);
                }

                RequestBody deleted_files = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(deleted_list));

                String added_size = String.valueOf(added_files.size());
                RequestBody added_files_size = RequestBody.create(MediaType.parse("text/plain"),added_size);

                String deleted_size = String.valueOf(deleted_list.size());
                RequestBody deleted_files_size = RequestBody.create(MediaType.parse("text/plain"),deleted_size);

                hashMap.put("deleted_list",deleted_files);
                hashMap.put("deleted_count",deleted_files_size);
                hashMap.put("added_count", added_files_size);

                Retrofit_edit_post retrofit_edit_post = getApiClient().create(Retrofit_edit_post.class);
                Call<String> upload_edit = retrofit_edit_post.edit_comm(added_files, hashMap);
                upload_edit.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            Intent intent = new Intent(comm_post_edit.this,comm_post_read.class);

                            intent.putExtra("post_authNum",post_authNum);
                            intent.putExtra("authNum",getIntent().getStringExtra("authNum"));
                            startActivity(intent);
                            finish();

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
        });
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

    private String getAuthNum() {
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
        rv = findViewById(R.id.comm_edit_rv);

        adapter = new imageAdapter_edit_comm(comm_post_edit.this,list,deleted_list,added_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(comm_post_edit.this,RecyclerView.HORIZONTAL,false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
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
                        adapter = new imageAdapter_edit_comm(comm_post_edit.this,list,deleted_list,added_list);
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(manager);

                        image_num.setText(list.size() + " / 10");


                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(comm_post_edit.this,comm_post_read.class);

        intent.putExtra("post_authNum",getIntent().getStringExtra("post_authNum"));
        intent.putExtra("authNum", authNum);
        intent.putExtra("category",getIntent().getStringExtra("category"));

        startActivity(intent);
        finish();
    }
}