package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;
import static com.chanyoung.market.signup_profile.url;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class edit_profile extends AppCompatActivity {

    Button submit;
    EditText input_name;
    ImageView imageView;
    TextView duplicate;

    public String set_name;
    public String authNum;
    public String isDuplicated;
    public String isDeleted = "no";
    MultipartBody.Part multipart;
    List<profileItems> list = new ArrayList<>();
    boolean isBitmap = true;
    boolean hasCamPerm;
    boolean hasWritePerm;
    Uri uri;
    RequestBody del_body;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        submit = (Button) findViewById(R.id.edit_profile_submit);
        input_name = (EditText) findViewById(R.id.edit_profile_input);
        imageView = (ImageView) findViewById(R.id.edit_profile_image);
        duplicate = (TextView) findViewById(R.id.edit_profile_dup);

        hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

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

        SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);

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
                if (response.isSuccessful() && response.body() != null) {
                    list = response.body();

                    for (int i = 0; i < list.size(); i++) {

                        String username = list.get(i).getUsername();
                        String path = list.get(i).getPath();

                        input_name.setText(username);

                        if (path == null) {
                            Glide.with(edit_profile.this)
                                    .load(url)
                                    .into(imageView);
                        } else {
                            Glide.with(edit_profile.this)
                                    .load(path)
                                    .into(imageView);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<profileItems>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(edit_profile.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(edit_profile.this);

                builder.setTitle("선택하세요.");

                builder.setItems(R.array.LAN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0:
                                Gallery();
                                break;

                            case 1:
                                TakePhoto();
                                break;

                            case 2:
                                Glide.with(edit_profile.this)
                                        .load(url)
                                        .into(imageView);
                                isDeleted = "Yes";
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
                builder.setNegativeButton("취소", cancel);
                alertDialog.show();

            }
        });

        Retrofit_basic retrofit_basic = retrofit.create(Retrofit_basic.class);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(!isBitmap) {

                        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                        Uri uri = getUri(edit_profile.this, bitmap);

                        String path = getAbsolutePath(uri);

                        File file = BitmapToFile(bitmap,path);

                        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"),file);
                        multipart = MultipartBody.Part.createFormData("uploaded_file",file.getName(),requestBody);
                    }
                    HashMap<String,RequestBody> hashMap = new HashMap<>();

                    RequestBody auth = RequestBody.create(MediaType.parse("text/plain"),authNum);
                    RequestBody username = RequestBody.create(MediaType.parse("text/plain"),input_name.getText().toString());

                    if(isDeleted != null) {
                         del_body = RequestBody.create(MediaType.parse("text/plain"),isDeleted);
                    }

                    hashMap.put("authNum",auth);
                    hashMap.put("username",username);
                    hashMap.put("isDeleted",del_body);

                    Call<String> call = retrofit_basic.edit_profile(multipart,hashMap);

                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful() && response.body() != null) {

                                if(response.body().equals("O")) {
                                    Intent intent = new Intent(edit_profile.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(edit_profile.this, "중복된 이름이 있습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d(TAG,t.getMessage());
                        }
                    });


            }
        });


    }

    private HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
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
        startActivityForResult(intent, 222);
    }
    private Uri getUri(Context context, Bitmap bitmap) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"IMG_"+ Calendar.getInstance().getTime(),null);

        return Uri.parse(path);
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


    ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            imageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 222 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();


            Bitmap image = (Bitmap) extras.get("data");


            Uri uri = getUri(getApplicationContext(),image);
            Log.e(TAG, "URI => " + uri);

            imageView.setImageBitmap(image);
        }
    }
}