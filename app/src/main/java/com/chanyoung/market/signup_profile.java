package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.login.SERVER_ADDRESS;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
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
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class signup_profile extends AppCompatActivity {

    ImageView profileImage;

    EditText input_email;
    EditText input_username;

    boolean hasCamPerm;
    boolean hasWritePerm;

    TextView duplicate_check_tv;

    SavedSharedPreferences preferences = new SavedSharedPreferences();

    SharedPreferences prefs;

    MultipartBody.Part multipartBody;

    String result;

    Uri uri;

    boolean isBitmap = true;

    Button submit;
    static final String url = SERVER_ADDRESS + "image/basicprofile.jpeg";
    /* OnCreate */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profile);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        submit = (Button) findViewById(R.id.signup_profile_submit);

        duplicate_check_tv = (TextView) findViewById(R.id.check_duplicate_tv);

       if(duplicate_check_tv.getText().toString().equals("사용 가능한 이름입니다.")) {
           result = "O";
       } else {
           result = "X";
       }


        hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        profileImage = (ImageView) findViewById(R.id.signup_profile_image);

        input_email = (EditText) findViewById(R.id.signup_input_email);
        input_username = (EditText) findViewById(R.id.signup_input_username);

        input_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Retrofit_duplicate retrofit_duplicate = retrofit.create(Retrofit_duplicate.class);

                String username = input_username.getText().toString();
                Call<String> call = retrofit_duplicate.InsertUsername(username);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.body().equals("X")) {
                            duplicate_check_tv.setText("중복된 이름 입니다.");
                            duplicate_check_tv.setTextColor(Color.parseColor("#FF0000"));
                            result = "X";
                        } else {
                            duplicate_check_tv.setText("사용 가능한 이름입니다.");
                            duplicate_check_tv.setTextColor(Color.parseColor("#FF21A82C"));
                            result = "O";
                        }

                        if(input_username.getText().toString().equals("")) {
                            duplicate_check_tv.setText("");
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!hasCamPerm || !hasWritePerm) {
                    ActivityCompat.requestPermissions(signup_profile.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(signup_profile.this);

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

                            case 2:
                                Glide.with(signup_profile.this)
                                        .load(url)
                                        .into(profileImage);
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

        Glide.with(signup_profile.this)
                .load(url)
                .into(profileImage);

        Retrofit_signup php = retrofit.create(Retrofit_signup.class);

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                Intent getIntent = getIntent();

                try {
                    String email = input_email.getText().toString();
                    String username = input_username.getText().toString();
                    String phone = getIntent.getStringExtra("phone");

                    String area = getIntent.getStringExtra("area");
                    String longitude = getIntent.getStringExtra("longitude");
                    String latitude = getIntent.getStringExtra("latitude");

                    if(result.equals("O")) {
                        Intent intent = new Intent(signup_profile.this,MainActivity.class);

                        if(!isBitmap) {
                            profileImage = (ImageView) findViewById(R.id.signup_profile_image);

                            Bitmap bitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
                            // 이미지뷰에서 비트맵 가져옴
                            Uri uri = getUri(getApplicationContext(),bitmap);
                            // 가져온 비트맵의 uri 가져옴.

                            String path = getAbsolutePath(uri);
                            // 가져온 비트맵 uri 로 이미지 절대경로 값 가져오기

                            File file = BitmapToFile(bitmap,path);
                            // 만든 절대경로/ 비트맵으로 파일을 만들어줌.

                            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"),file);
                            multipartBody= MultipartBody.Part.createFormData("uploaded_file",file.getName(),requestBody);
                             //file.getName 은 사진 가져오거나 촬영시에 자동으로 생성되는 이름 가져와서 보냄.
                        }

                        HashMap<String,RequestBody> hashMap = new HashMap<>();

                        RequestBody email_body = RequestBody.create(MediaType.parse("text/plain"),email);
                        RequestBody name_body = RequestBody.create(MediaType.parse("text/plain"),username);
                        RequestBody phone_body = RequestBody.create(MediaType.parse("text/plain"),phone);
                        RequestBody area_body = RequestBody.create(MediaType.parse("text/plain"),area);
                        RequestBody lng_body = RequestBody.create(MediaType.parse("text/plain"),longitude);
                        RequestBody lat_body = RequestBody.create(MediaType.parse("text/plain"),latitude);

                        hashMap.put("email",email_body);
                        hashMap.put("username",name_body);
                        hashMap.put("phone",phone_body);
                        hashMap.put("area",area_body);
                        hashMap.put("longitude",lng_body);
                        hashMap.put("latitude",lat_body);


                        Retrofit_file retrofit_file = retrofit.create(Retrofit_file.class);
                        Call<String> call_file = retrofit_file.requestFile(multipartBody,hashMap);
                        call_file.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.isSuccessful() && response.body() != null) {

                                    long now = System.currentTimeMillis();
                                    Date mDate = new Date(now);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    String getTime = simpleDateFormat.format(mDate).toString();

                                    String authNum = response.body();

                                    String save_form = "{\"authNum\":\""+response.body()+"\"}";
                                    preferences.clearUserName(getApplicationContext(),"Username");
                                    preferences.setUserName(signup_profile.this,getTime,save_form);

                                    preferences.clearSavedState(getApplicationContext(),"savedState");
                                    String save_form_progress = "{\"progress\":\""+5+"\"}";
                                    preferences.clearSavedState(signup_profile.this,"savedState");
                                    preferences.save_state(signup_profile.this, "savedState",save_form_progress);

                                    Retrofit_insertAddress retrofit_insertAddress = retrofit.create(Retrofit_insertAddress.class);
                                    Call<String> insertAddress = retrofit_insertAddress.insertAddress(authNum,area,longitude,latitude);
                                    insertAddress.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if(response.isSuccessful() && response.body() != null) {
                                                System.out.println(response.body());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            System.out.println(t.getMessage());
                                        }
                                    });
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                            }
                        });
                    } else {
                        Toast.makeText(signup_profile.this, "이름 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                            profileImage.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
    );

    private HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 222 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();


            Bitmap image = (Bitmap) extras.get("data");


            Uri uri = getUri(getApplicationContext(),image);
            Log.e(TAG, "URI => " + uri);

            profileImage.setImageBitmap(image);
        }
    }

}
