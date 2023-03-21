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
import android.view.WindowManager;
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
import retrofit2.http.Multipart;

public class upload_comm_post extends AppCompatActivity {

    Button upload;

    ImageButton imageButton;

    Spinner spinner = null;
    ArrayAdapter<CharSequence> spinner_adapter = null;

    EditText input_title;
    EditText input_content;

    SharedPreferences prefs;

    boolean hasCamPerm;
    boolean hasWritePerm;
    ArrayList<Uri> list = new ArrayList<>();
    List<BasicItems> basic = new ArrayList<>();

    RecyclerView rv;
    Comm_ImageAdapter adapter;
    LinearLayoutManager manager;

    TextView image_num;

    public String authNum;
    public String category;
    public String longitude;
    public String latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_comm_post);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        input_title = (EditText) findViewById(R.id.upload_comm_title);
        input_content = (EditText) findViewById(R.id.upload_comm_content);
        upload = (Button) findViewById(R.id.upload_comm_submit);
        imageButton = (ImageButton) findViewById(R.id.upload_comm_images);
        image_num = (TextView) findViewById(R.id.upload_comm_image_num);

        spinner = (Spinner) findViewById(R.id.upload_comm_spinner);
        spinner_adapter = ArrayAdapter.createFromResource(
                this,
                R.array.comm_category,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        );
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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(upload_comm_post.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

                rv = (RecyclerView) findViewById(R.id.upload_comm_images_rv);

                adapter = new Comm_ImageAdapter(upload_comm_post.this,list);
                manager = new LinearLayoutManager(upload_comm_post.this,LinearLayoutManager.HORIZONTAL,false);

                AlertDialog.Builder builder = new AlertDialog.Builder(upload_comm_post.this);

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
        Retrofit_upload_check_auth check = getApiClient().create(Retrofit_upload_check_auth.class);
        Call<String> getArea = check.checkAuth(getAuthNum());

        getArea.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null) {
                    input_content.setHint(response.body() + "에 관련된 질문이나 이야기를 해보세요.");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_title.getText().toString().equals("")) {
                    Toast.makeText(upload_comm_post.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (input_content.getText().toString().equals("")) {
                    Toast.makeText(upload_comm_post.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    String title = input_title.getText().toString();
                    String content = input_content.getText().toString();

                    RequestBody title_body = RequestBody.create(MediaType.parse("text/plain"),title);
                    RequestBody content_body = RequestBody.create(MediaType.parse("text/plain"),content);
                    RequestBody cat_body = RequestBody.create(MediaType.parse("text/plain"),category);
                    RequestBody auth_body = RequestBody.create(MediaType.parse("text/plain"),getAuthNum());
                    RequestBody count_body = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(list.size()));

                    HashMap<String,RequestBody> hashMap = new HashMap<>();

                    hashMap.put("title", title_body);
                    hashMap.put("content",content_body);
                    hashMap.put("category",cat_body);
                    hashMap.put("authNum",auth_body);
                    hashMap.put("count",count_body);
                    ArrayList<MultipartBody.Part> files = new ArrayList<>();

                    for(int i = 0; i <list.size(); i++) {
                        Uri uri = list.get(i);

                        String path = getAbsolutePath(uri);

                        File file = new File(path);

                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"),file);

                        String fileName = "photo" + i + ".jpg";

                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file"+i, fileName, fileBody);

                        files.add(filePart);

                    }

                    Retrofit_load_post upload = getApiClient().create(Retrofit_load_post.class);
                    Call<String> call = upload.upload_comm(files,hashMap);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful() && response.body() != null) {
                                Log.d("게시글 업로드", response.body());
                                Intent intent = new Intent(upload_comm_post.this,comm_post_read.class);
                                intent.putExtra("post_authNum",response.body());
                                intent.putExtra("authNum",getAuthNum());
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("게시글 업로드 실패",t.getMessage());
                        }
                    });

                }
            }
        });



    }

    public String getAuthNum() {
        prefs = getSharedPreferences("Username",MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            authNum = jsonObject.getString("authNum");
        } catch(JSONException e) {
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
                        adapter = new Comm_ImageAdapter(upload_comm_post.this,list);
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(manager);

                        image_num.setText(list.size() + " / 10");


                    }
                }
            }
        }
    }
}