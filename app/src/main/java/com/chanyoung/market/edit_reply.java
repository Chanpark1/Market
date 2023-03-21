package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

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

public class edit_reply extends AppCompatActivity {

    public String authNum;
    public String post_authNum;
    public String reply_authNum;

    EditText editText;
    FrameLayout frameLayout;
    ImageView imageView;

    ImageButton load_image;
    ImageButton delete_image;

    Button submit;

    public boolean isBitmap = true;

    List<ReplyItems> list = new ArrayList<>();

    Uri uri = null;

    MultipartBody.Part multipartBody;

    public String isDeleted = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reply);

        editText = (EditText) findViewById(R.id.edit_reply_input);
        imageView = (ImageView) findViewById(R.id.edit_reply_image);
        frameLayout = (FrameLayout) findViewById(R.id.edit_reply_frame);

        load_image = (ImageButton) findViewById(R.id.edit_reply_load_image);
        delete_image = (ImageButton) findViewById(R.id.edit_reply_delete_image);

        submit = (Button) findViewById(R.id.edit_reply_submit);

        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);

        authNum = getIntent().getStringExtra("authNum");
        post_authNum = getIntent().getStringExtra("post_authNum");
        reply_authNum = getIntent().getStringExtra("reply_authNum");

        Call<List<ReplyItems>> call = retrofit_load_post.load_origin_reply(post_authNum,reply_authNum);
        call.enqueue(new Callback<List<ReplyItems>>() {
            @Override
            public void onResponse(Call<List<ReplyItems>> call, Response<List<ReplyItems>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    list = response.body();

                    for(int i = 0; i < list.size(); i++) {
                        String path = list.get(i).getImage_path();
                        String content = list.get(i).getContent();

                        if(path != null) {
                            frameLayout.setVisibility(View.VISIBLE);

                            Glide.with(edit_reply.this)
                                    .load(path)
                                    .into(imageView);
                        }

                        editText.setText(content);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ReplyItems>> call, Throwable t) {
                Log.d(TAG, t.getMessage());

            }
        });

        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(0);
                delete_image.setVisibility(View.GONE);
                isDeleted = "YES";
                isBitmap = true;
            }
        });

        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(edit_reply.this);

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isBitmap) {

                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                    Uri uri = getUri(edit_reply.this,bitmap);

                    String path = getAbsolutePath(uri);

                    File file = BitmapToFile(bitmap,path);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"),file);

                    multipartBody = MultipartBody.Part.createFormData("uploaded_file",file.getName(),requestBody);
                }

                HashMap<String,RequestBody> hashMap = new HashMap<>();
                String contents = editText.getText().toString();
                RequestBody content_body = RequestBody.create(MediaType.parse("text/plain"),contents);
                RequestBody reply_auth = RequestBody.create(MediaType.parse("text/plain"),reply_authNum);
                RequestBody post_auth = RequestBody.create(MediaType.parse("text/plain"),post_authNum);
                RequestBody is_body = RequestBody.create(MediaType.parse("text/plain"),isDeleted);
                hashMap.put("content",content_body);
                hashMap.put("reply_authNum",reply_auth);
                hashMap.put("post_authNum",post_auth);
                hashMap.put("isDeleted",is_body);


                Call<String> call = retrofit_load_post.update_reply(multipartBody,hashMap);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            Intent intent = new Intent(edit_reply.this, comm_post_read.class);

                            intent.putExtra("post_authNum",post_authNum);
                            intent.putExtra("authNum",getIntent().getStringExtra("authNum"));

                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
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

    private Uri getUri(Context context, Bitmap bitmap) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"IMG_"+ Calendar.getInstance().getTime(),null);

        return Uri.parse(path);
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

    private String getAuthNum() {
        SharedPreferences prefs = getSharedPreferences("Username",Context.MODE_PRIVATE);

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
                            delete_image.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(bitmap);
                            isDeleted = "NO";

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
            delete_image.setVisibility(View.VISIBLE);  frameLayout.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(image);
            isDeleted = "NO";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(edit_reply.this, comm_post_read.class);
        intent.putExtra("authNum",authNum);
        intent.putExtra("post_authNum",post_authNum);

        startActivity(intent);
        finish();
    }
}